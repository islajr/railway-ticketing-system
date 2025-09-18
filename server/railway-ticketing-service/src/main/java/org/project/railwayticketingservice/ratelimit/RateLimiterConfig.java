package org.project.railwayticketingservice.ratelimit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BandwidthBuilder;
import io.github.bucket4j.BucketConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimiterConfig {

    @Value("${app.rate.capacity}")
    long capacity;

    @Value("${app.rate.refill.tokens}")
    long refillTokens;

    @Value("${app.rate.refill.duration.minutes}")
    long refillDurationMinutes;

    @Bean
    public BucketConfiguration getBucketConfiguration() {
        /* 100 requests per minute */
        Bandwidth limit = BandwidthBuilder.builder()
                .capacity(capacity).
                refillIntervally(refillTokens, Duration.ofMinutes(refillDurationMinutes))
                .build();

        return BucketConfiguration.builder()
                .addLimit(limit)
                .build();
    }
}
