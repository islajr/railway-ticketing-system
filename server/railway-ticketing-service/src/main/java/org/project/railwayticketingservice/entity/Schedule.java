package org.project.railwayticketingservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "schedules")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.EAGER)
    private Train train;

    @OneToMany
    private List<ScheduleSeat> seats;

    // capacity and seat selection
    @Column(nullable = false, name = "current_capacity")
    private Long currentCapacity;   // reduces with each successful reservation

    @Column(nullable = false, name = "is_full")
    private boolean isFull;

    @Column(nullable = false, name = "departure_time")
    private LocalDateTime departureTime;

    @Column(nullable = false, name = "arrival_time")
    private LocalDateTime arrivalTime;

    @Column(nullable = false, name = "origin")
    private String origin;

    @Column(nullable = false, name = "destination")
    private String destination;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    LocalDateTime updatedAt;

    // get empty seats
    public List<ScheduleSeat> getEmptySeats() {
        List<ScheduleSeat> emptySeats = new ArrayList<>();
        for (ScheduleSeat seat : seats) {
            if (!seat.isReserved())
                emptySeats.add(seat);
        }

        return emptySeats;
    };
}
