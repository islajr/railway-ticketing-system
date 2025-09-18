package org.project.railwayticketingservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.railwayticketingservice.dto.app.request.NewStationRequest;
import org.project.railwayticketingservice.dto.app.request.StationUpdateRequest;
import org.project.railwayticketingservice.dto.app.response.StationResponse;
import org.project.railwayticketingservice.entity.Station;
import org.project.railwayticketingservice.exception.exceptions.RtsException;
import org.project.railwayticketingservice.repository.StationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class StationService {

    private final StationRepository stationRepository;

    public ResponseEntity<StationResponse> createStation(NewStationRequest request) {

        if (stationRepository.existsByName(request.name())) {
            throw new RtsException(HttpStatus.CONFLICT, "Station with name " + request.name() + " already exists");
        } else {  // continue only if station with said name does not exist
            Station station = Station.builder()
                    .name(request.name())
                    .code(request.code())
                    .LGA(request.LGA())
                    .isActive(true)
                    .build();
            stationRepository.save(station);
            log.info("Successfully created station: {}", station.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(StationResponse.from(station));
        }
    }

    public ResponseEntity<StationResponse> deleteStation(Long stationId) {
        Station station = stationRepository.findById(stationId).orElseThrow(() -> new RtsException(HttpStatus.NOT_FOUND, "Station with id " + stationId + " does not exist"));

        stationRepository.delete(station);

        log.info("Successfully deleted station: {}", station.getName());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    public ResponseEntity<StationResponse> updateStation(Long id, StationUpdateRequest request) {

        /* updatable station attributes:
        *
        * - name
        * - code
        * - isActive
        * */

        Station station = stationRepository.findById(id).orElseThrow(() -> new RtsException(HttpStatus.NOT_FOUND, "Station not found"));

        if (!Objects.equals(request.name(), "null") && !station.getName().equals(request.name())) {
            station.setName(request.name());
            log.info("Successfully updated station name: {}", station.getName());
        }
        if (!Objects.equals(request.code(), "null") && !station.getCode().equals(request.code())) {
            station.setCode(request.code());
            log.info("Successfully updated station code: {}", station.getCode());
        }
        if (!Objects.equals(request.isActive(), "null") && !String.valueOf(station.isActive()).equals(request.isActive())) {
            station.setActive(Boolean.parseBoolean(request.isActive().strip()));
            log.info("Successfully updated station's active status: {}", station.isActive());
        }

        stationRepository.save(station);

        log.info("Successfully updated station: {}", station.getName());
        return ResponseEntity.status(HttpStatus.OK).body(
                StationResponse.from(station)
        );
    }

    public ResponseEntity<StationResponse> getStation(Long id) {
        Station station = stationRepository.findById(id).orElseThrow(() -> new RtsException(HttpStatus.NOT_FOUND, "Station not found"));

        log.info("Successfully retrieved station: {}", station.getName());
        return ResponseEntity.status(HttpStatus.OK).body(StationResponse.from(station));

    }

    public ResponseEntity<List<StationResponse>> getAllStations() {

        List<Station> stations = stationRepository.findAll();

        if (stations.isEmpty()) {
            log.warn("No stations found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<StationResponse> stationResponses = new ArrayList<>();

        for (Station station : stations) {
            stationResponses.add(StationResponse.from(station));
        }

        log.info("Successfully retrieved {} stations", stationResponses.size());
        return ResponseEntity.status(HttpStatus.OK).body(stationResponses);
    }
}
