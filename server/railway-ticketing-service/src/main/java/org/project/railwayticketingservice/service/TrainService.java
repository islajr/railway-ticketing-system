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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TrainService {

    private final TrainRepository trainRepository;

    // admin-specific method
    public ResponseEntity<NewTrainResponse> createNewTrain(NewTrainRequest newTrainRequest) {
        if (trainRepository.existsByName(newTrainRequest.name())) {
            throw new RtsException(409, "train name already exists!");
        } else {
            Train train = Train.builder()
                    .name(newTrainRequest.name())
                    .capacity(Long.valueOf(newTrainRequest.capacity().strip()))
                    .schedules(null)    // no schedules for now for new train
                    .isActive(true)
                    .build();
            trainRepository.save(train);
            System.out.println("train successfully created");
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    NewTrainResponse.from(train)
            );

        }
    }

    public ResponseEntity<NewTrainResponse> getTrain(String id) {
        Train train = trainRepository.findTrainById(Long.getLong(id)).orElseThrow(
                () -> new RtsException(404, "Train not found!")
        );

        return ResponseEntity.status(HttpStatus.OK).body(
                NewTrainResponse.from(train)
        );
    }

    public ResponseEntity<NewTrainResponse> updateTrain(String id, TrainUpdateRequest request) {
        /* update-able attributes for now include:
        *
        * - name
        * - is active
        * */

        Train train = trainRepository.findById(Long.valueOf(id)).orElseThrow(() -> new RtsException(404, "Train not found!"));

        if (!Objects.equals(request.name(), "null") && !train.getName().equals(request.name())) {
            train.setName(request.name());
        }

        if (!Objects.equals(request.isActive(), "null") && !String.valueOf(train.isActive()).equals(request.isActive())) {
            train.setActive(Boolean.parseBoolean(request.isActive().strip()));
        }

        trainRepository.save(train);
        return ResponseEntity.status(HttpStatus.OK).body(
                NewTrainResponse.from(train));
    }

    public ResponseEntity<NewTrainResponse> removeTrain(String id) {    // cross-check method during testing
        Train train = trainRepository.findById(Long.valueOf(id)).orElseThrow(() -> new RtsException(404, "Train not found!"));

        trainRepository.delete(train);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    public ResponseEntity<List<NewTrainResponse>> getAllTrains() {
        List<Train> trains = trainRepository.findAll();

        if (trains.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<NewTrainResponse> trainResponses = new ArrayList<>();

        for (Train train : trains) {
            trainResponses.add(NewTrainResponse.from(train));
        }
        return ResponseEntity.status(HttpStatus.OK).body(trainResponses);
    }
}
