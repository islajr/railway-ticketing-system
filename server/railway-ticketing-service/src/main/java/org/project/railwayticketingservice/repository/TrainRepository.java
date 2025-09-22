package org.project.railwayticketingservice.repository;

import org.project.railwayticketingservice.entity.Train;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrainRepository extends JpaRepository<Train, Long> {
    Optional<Train> findTrainByName(String train);

    boolean existsByName(String name);

    Optional<Train> findTrainById(Long id);
}
