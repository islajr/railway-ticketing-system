package org.project.railwayticketingservice.ratelimit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    @Value("${app.rate.capacity}")
    long capacity;

    @Value("${app.rate.refill.tokens}")
    long refillTokens;

    @Value("${app.rate.refill.duration.minutes}")
    long refillDurationMinutes;

    // in-memory storage
    Map<String, Bucket> storage = new ConcurrentHashMap<>();

    /**
     * Bucket creation logic
     * */
    Bucket createBucket() {

        /* Initial limit: 100 requests per minute */

        Bandwidth limit = Bandwidth.builder()
                .capacity(capacity)
                .refillIntervally(refillTokens, Duration.ofMinutes(refillDurationMinutes))
                .build();

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    /**
     * Building key used for the bucket.
     * Prefer authenticated JWT claims; Fallback to client IP
     */
    private String clientKey(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {   // early exit for client Ip
            return "ip" + clientIp(request);
        } else {

            // main logic for getting JWT subject
            String name = authentication.getName();
            return "user: " + name;
        }
    }

    private String clientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");

        if (xff == null || xff.isBlank()) {
            return request.getRemoteAddr();
        }

        // header may contain a comma-separated list where the first value is the original client
        return xff.split(",")[0].trim();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String key = clientKey(request);
        
        Bucket bucket = storage.computeIfAbsent(key, b -> createBucket());

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        long nanosToWait = Math.max(0L, probe.getNanosToWaitForRefill());
        long secondsToWait = (nanosToWait + 999_999_999L) / 1_000_000_000L;
        long resetEpochSeconds = Instant.now().getEpochSecond() + secondsToWait;

        if (probe.isConsumed()) {
            response.setHeader("X-RateLimit-Remaining", String.valueOf(probe.getRemainingTokens()));
            response.setHeader("X-RateLimit-Reset", String.valueOf(resetEpochSeconds));
            response.setHeader("X-RateLimit-Limit", String.valueOf(capacity));

            filterChain.doFilter(request, response);
        } else {    // over the limit?
            response.setStatus(429);
            response.setContentType("application/json");
            response.setHeader("X-RateLimit-Remaining", String.valueOf(probe.getRemainingTokens()));
            response.setHeader("X-RateLimit-Limit", String.valueOf(capacity));
            response.setHeader("Retry-After", String.valueOf(secondsToWait));
            response.setHeader("X-RateLimit-Retry-After-Seconds", String.valueOf(secondsToWait));
            response.getWriter().write("""
                    {
                        "status": "429",
                        "error": "Too Many Requests",
                        "message": "You have exceeded your rate limit. Try again later.
                        "retryAfterSeconds": %d,
                        "timestamp": %s,
                        "path": %s
                    }
                    """.formatted(secondsToWait, LocalDateTime.now(), request.getRequestURI()));
        }
    }
}
