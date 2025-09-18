package org.project.railwayticketingservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class TokenService {
    private final Set<String> disallowedTokens = new HashSet<>();

    public void disallowToken(String token) {
        disallowedTokens.add(token);
        log.info("Disallowed access token: {}", token);
    }

    public boolean isTokenAllowed(String token) {
        return !disallowedTokens.contains(token);
    }
}
