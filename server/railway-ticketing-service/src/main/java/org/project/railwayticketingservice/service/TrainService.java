package org.project.railwayticketingservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.railwayticketingservice.dto.app.request.NewTrainRequest;
import org.project.railwayticketingservice.dto.app.request.TrainUpdateRequest;
import org.project.railwayticketingservice.dto.app.response.NewTrainResponse;
import org.project.railwayticketingservice.entity.Train;
import org.project.railwayticketingservice.exception.exceptions.RtsException;
import org.project.railwayticketingservice.repository.TrainRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainService {

    private final TrainRepository trainRepository;

    // admin-specific method
    @Cacheable(value = "trains")
    public ResponseEntity<NewTrainResponse> createNewTrain(NewTrainRequest newTrainRequest) {
        if (trainRepository.existsByName(newTrainRequest.name())) {
            throw new RtsException(HttpStatus.CONFLICT, "train name already exists!");
        } else {
            Train train = Train.builder()
                    .name(newTrainRequest.name())
                    .capacity(Long.valueOf(newTrainRequest.capacity().strip()))
                    .schedules(null)    // no schedules for now for new train
                    .isActive(true)
                    .build();
            trainRepository.save(train);
            log.info("Train '{}' successfully created", train.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    NewTrainResponse.from(train)
            );

        }
    }

    @Cacheable(value = "trains", key = "#id")
    public ResponseEntity<NewTrainResponse> getTrain(String id) {
        Train train = trainRepository.findTrainById(Long.getLong(id)).orElseThrow(
                () -> new RtsException(HttpStatus.NOT_FOUND, "Train not found!")
        );

        log.info("Train '{}' successfully retrieved", train.getName());
        return ResponseEntity.status(HttpStatus.OK).body(
                NewTrainResponse.from(train)
        );
    }

    @CachePut(value = "trains", key = "#id")
    public ResponseEntity<NewTrainResponse> updateTrain(String id, TrainUpdateRequest request) {
        /* update-able attributes for now include:
        *
        * - name
        * - is active
        * */

        Train train = trainRepository.findById(Long.valueOf(id)).orElseThrow(() -> new RtsException(HttpStatus.NOT_FOUND, "Train not found!"));

        if (!Objects.equals(request.name(), "null") && !train.getName().equals(request.name())) {
            train.setName(request.name());
            log.info("Train name - '{}' successfully updated", train.getName());
        }

        if (!Objects.equals(request.isActive(), "null") && !String.valueOf(train.isActive()).equals(request.isActive())) {
            train.setActive(Boolean.parseBoolean(request.isActive().strip()));
            log.info("Train - '{}' active status successfully updated", train.isActive());
        }

        trainRepository.save(train);
        log.info("Train '{}' successfully updated", train.getName());
        return ResponseEntity.status(HttpStatus.OK).body(
                NewTrainResponse.from(train));
    }

    @CacheEvict(value = "trains", key = "#id")
    public ResponseEntity<NewTrainResponse> removeTrain(String id) {    // cross-check method during testing
        Train train = trainRepository.findById(Long.valueOf(id)).orElseThrow(() -> new RtsException(HttpStatus.NOT_FOUND, "Train not found!"));

        trainRepository.delete(train);
        log.info("Train '{}' successfully removed", train.getName());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Cacheable(value = "#trains")
    public ResponseEntity<List<NewTrainResponse>> getAllTrains(int page, int size, String sortBy, String direction) {
        
        Sort sort = direction.equals("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Train> trains = trainRepository.findAll(pageable);

        if (trains.isEmpty()) {
            log.warn("Train list is empty");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<NewTrainResponse> trainResponses = new ArrayList<>();

        for (Train train : trains) {
            trainResponses.add(NewTrainResponse.from(train));
        }

        log.info("Successfully retrieved {} trains", trainResponses.size());
        return ResponseEntity.status(HttpStatus.OK).body(trainResponses);
    }
}
