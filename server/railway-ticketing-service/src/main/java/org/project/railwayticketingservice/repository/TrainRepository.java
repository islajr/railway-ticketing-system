package org.project.railwayticketingservice.repository;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.project.railwayticketingservice.entity.Train;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainRepository extends JpaRepository<Train, Long> {
    Train findTrainByName(String train);

    boolean existsByName(String name);

    Train findTrainById(String id);
}
