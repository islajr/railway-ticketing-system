package org.project.railwayticketingservice.ratelimit;

import java.io.IOException;
import java.time.Instant;
import java.util.function.Supplier;

import io.github.bucket4j.Bucket;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final ProxyManager<String> proxyManager;
    private final BucketConfiguration bucketConfiguration;

    public RateLimitFilter(ProxyManager<String> proxyManager, BucketConfiguration bucketConfiguration) {
        this.proxyManager = proxyManager;
        this.bucketConfiguration = bucketConfiguration;
    }

    /**
     * Building key used for the bucker.
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
        
        Supplier<BucketConfiguration> configSupplier = () -> bucketConfiguration;
        
        Bucket bucket = proxyManager.builder().build(key, configSupplier);

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        
        long nanosToWait = Math.max(0L, probe.getNanosToWaitForRefill());
        long secondsToWait = (nanosToWait + 999_999_999L) / 1_000_000_000L;
        long resetEpochSeconds = Instant.now().getEpochSecond() + secondsToWait;

        if (probe.isConsumed()) {
            response.setHeader("X-RateLimit-Remaining", String.valueOf(probe.getRemainingTokens()));
            response.setHeader("X-RateLimit-Reset", String.valueOf(resetEpochSeconds));
            response.setHeader("X-RateLimit-Limit", "100"); // should probably not hard-code it.

            filterChain.doFilter(request, response);
        } else {    // over the limit?
            response.setStatus(429);
            response.setContentType("application/json");
            response.setHeader("Retry-After", String.valueOf(secondsToWait));
            response.setHeader("X-RateLimit-Retry-After-Seconds", String.valueOf(secondsToWait));
            response.getWriter().write("""
                    {
                        "error": "429 - Too Many Requests",
                        "message": "You have exceeded your rate limit. Try again later.
                        "retryAfterSeconds": %d,
                    }
                    """.formatted(secondsToWait));
        }
    }
}
