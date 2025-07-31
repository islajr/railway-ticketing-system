package org.project.railwayticketingservice.repository;

import org.project.railwayticketingservice.entity.Train;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainRepository extends JpaRepository<Train, Long> {
    Train findTrainByName(String train);
}
