package org.project.railwayticketingservice.repository;

import org.project.railwayticketingservice.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> {
    Admin findAdminByEmail(String username);

    boolean existsByEmail(String email);
}
