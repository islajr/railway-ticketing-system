package org.project.railwayticketingservice.repository;

import java.util.Optional;

import org.project.railwayticketingservice.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StationRepository extends JpaRepository<Station, Long> {
    Optional<Station> findStationByName(String name);

    boolean existsByName(String name);
}
