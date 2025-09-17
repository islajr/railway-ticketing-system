package org.project.railwayticketingservice.ratelimit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BandwidthBuilder;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.grid.jcache.JCacheProxyManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.CacheManager;
import javax.cache.Caching;
import java.time.Duration;

@Configuration
public class RateLimiterConfig {

    /* configuring caching package */
    @Bean
    public CacheManager cacheManager() {
        return Caching.getCachingProvider().getCacheManager();
    }

    /* cache proxy config */
    @Bean
    public ProxyManager<String> proxyManager(CacheManager cacheManager) {
        return new JCacheProxyManager<>(cacheManager.getCache("rate-limit-buckets"));
    }

    @Bean
    public BucketConfiguration getBucketConfiguration() {
        /* 100 requests per minute */
        Bandwidth limit = BandwidthBuilder.builder()
                .capacity(100).
                refillIntervally(100, Duration.ofMinutes(1))
                .build();

        return BucketConfiguration.builder()
                .addLimit(limit)
                .build();
    }
}
