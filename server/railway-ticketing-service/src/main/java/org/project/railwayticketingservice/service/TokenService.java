package org.project.railwayticketingservice.service;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class TokenService {
    private final Set<String> disallowedTokens = new HashSet<>();

    public void disallowToken(String token) {
        disallowedTokens.add(token);
        // remember to invalidate refresh tokens.
    }

    public boolean isTokenAllowed(String token) {
        return !disallowedTokens.contains(token);
    }
}
