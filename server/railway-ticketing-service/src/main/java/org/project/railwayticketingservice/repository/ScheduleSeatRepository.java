package org.project.railwayticketingservice.repository;

import org.project.railwayticketingservice.entity.ScheduleSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleSeatRepository extends JpaRepository<ScheduleSeat, Long> {

    ScheduleSeat findBySeatId(Long seatId);
    ScheduleSeat findByLabel(String label);
}
