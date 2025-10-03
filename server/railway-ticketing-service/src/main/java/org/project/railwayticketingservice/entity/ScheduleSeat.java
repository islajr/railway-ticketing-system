package org.project.railwayticketingservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ScheduleSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String label;

    private boolean isReserved;

    @ManyToOne
    private Schedule schedule;

    @OneToOne
    private Reservation reservation;

}
