package org.project.railwayticketingservice.service;

import lombok.RequiredArgsConstructor;
import org.project.railwayticketingservice.dto.app.request.NewReservationRequest;
import org.project.railwayticketingservice.dto.app.response.ReservationResponse;
import org.project.railwayticketingservice.dto.app.response.TrainScheduleResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppService {
    public ResponseEntity<ReservationResponse> createReservation(NewReservationRequest request) {

    }

    public ResponseEntity<ReservationResponse> getReservation(String id) {
    }

    public ResponseEntity<List<ReservationResponse>> getAllReservations() {
    }

    public ResponseStatus deleteReservation(String id) {
    }

    public ResponseStatus deleteAllReservations() {
    }

    public ResponseEntity<List<TrainScheduleResponse>> getTrainSchedules(String filter) {
    }
}
