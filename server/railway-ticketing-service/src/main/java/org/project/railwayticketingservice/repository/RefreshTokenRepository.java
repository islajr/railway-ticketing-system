package org.project.railwayticketingservice.repository;

import org.project.railwayticketingservice.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    Optional<RefreshToken> findRefreshTokenByToken(String token);
    RefreshToken findRefreshTokenByEmail(String email);
    void deleteRefreshTokenByEmail(String email);
}
