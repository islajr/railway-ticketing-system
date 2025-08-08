package org.project.railwayticketingservice.service;

import lombok.RequiredArgsConstructor;
import org.project.railwayticketingservice.dto.app.request.NewTrainRequest;
import org.project.railwayticketingservice.dto.app.request.TrainUpdateRequest;
import org.project.railwayticketingservice.dto.app.response.NewTrainResponse;
import org.project.railwayticketingservice.entity.Train;
import org.project.railwayticketingservice.exception.RtsException;
import org.project.railwayticketingservice.repository.TrainRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainService {

    private final TrainRepository trainRepository;

    // admin-specific method
    public ResponseEntity<NewTrainResponse> createNewTrain(NewTrainRequest newTrainRequest) {
        if (!trainRepository.existsByName(newTrainRequest.name())) {
            Train train = Train.builder()
                    .name(newTrainRequest.name())
                    .capacity(Long.valueOf(newTrainRequest.capacity().strip()))
                    .schedules(null)    // no schedules for now for new train
                    .build();
            trainRepository.save(train);
            System.out.println("train successfully created");
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    NewTrainResponse.builder()
                            .trainId(train.getId().toString())
                            .trainName(train.getName())
                            .capacity(train.getCapacity().toString())
                            .build()
            );

        } throw new RtsException(409, "train name already exists!");
    }

    public ResponseEntity<NewTrainResponse> getTrain(String id) {
        Train train = trainRepository.findTrainById(Long.getLong(id));

        if (train != null) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    NewTrainResponse.builder()
                            .trainId(train.getId().toString())
                            .trainName(train.getName())
                            .capacity(train.getCapacity().toString())
                            .build()
            );
        } throw new RtsException(404, "Train not found!");
    }

    public ResponseEntity<NewTrainResponse> updateTrain(String id, TrainUpdateRequest request) {
    }

    public ResponseEntity<NewTrainResponse> removeTrain(String id) {
    }

    public ResponseEntity<List<Train>> getAllTrains() {
    }
}
