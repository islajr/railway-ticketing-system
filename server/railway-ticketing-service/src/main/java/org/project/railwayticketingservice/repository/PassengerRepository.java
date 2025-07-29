package org.project.railwayticketingservice.repository;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.project.railwayticketingservice.entity.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Integer> {
    Passenger findPassengerByEmail(String username);

    boolean existsByEmail(@NotNull(message = "please provide an e-mail address") @NotBlank(message = "e-mail address field cannot be blank") @Email(message = "please provide a valid e-mail address") @Size(max = 80, message = "e-mail address cannot be more than 80 characters") String email);
}
