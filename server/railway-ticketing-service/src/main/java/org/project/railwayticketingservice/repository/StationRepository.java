package org.project.railwayticketingservice.repository;

import org.project.railwayticketingservice.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StationRepository extends JpaRepository<Station, Long> {
    Station getStationByName(String name);

    boolean existsByName(String name);
}
