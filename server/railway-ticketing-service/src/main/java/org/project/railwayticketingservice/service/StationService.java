package org.project.railwayticketingservice.service;

import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class StationService {

    private final StationRepository stationRepository;

    public ResponseEntity<StationResponse> createStation(NewStationRequest request) {

        if (stationRepository.existsByName(request.name())) {
            throw new RtsException(409, "Station with name " + request.name() + " already exists");
        } else {  // continue only if station with said name does not exist
            Station station = Station.builder()
                    .name(request.name())
                    .code(request.code())
                    .LGA(request.LGA())
                    .isActive(true)
                    .build();
            System.out.println("created station: " + station.getName());
            stationRepository.save(station);
            return ResponseEntity.status(HttpStatus.CREATED).body(StationResponse.from(station));
        }
    }

    public ResponseEntity<StationResponse> deleteStation(Long stationId) {
        Station station = stationRepository.findById(stationId).orElseThrow(() -> new RtsException(404, "Station with id " + stationId + " does not exist"));

        stationRepository.delete(station);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    public ResponseEntity<StationResponse> updateStation(Long id, StationUpdateRequest request) {

        /* updatable station attributes:
        *
        * - name
        * - code
        * - isActive
        * */

        Station station = stationRepository.findById(id).orElseThrow(() -> new RtsException(404, "Station not found"));

        if (!Objects.equals(request.name(), "null") && !station.getName().equals(request.name())) {
            station.setName(request.name());
        }
        if (!Objects.equals(request.code(), "null") && !station.getCode().equals(request.code())) {
            station.setCode(request.code());
        }
        if (!Objects.equals(request.isActive(), "null") && !String.valueOf(station.isActive()).equals(request.isActive())) {
            station.setActive(Boolean.parseBoolean(request.isActive().strip()));
        }

        stationRepository.save(station);
        return ResponseEntity.status(HttpStatus.OK).body(
                StationResponse.from(station)
        );
    }

    public ResponseEntity<StationResponse> getStation(Long id) {
        Station station = stationRepository.findById(id).orElseThrow(() -> new RtsException(404, "Station not found"));

        return ResponseEntity.status(HttpStatus.OK).body(StationResponse.from(station));

    }

    public ResponseEntity<List<StationResponse>> getAllStations() {

        List<Station> stations = stationRepository.findAll();

        if (stations.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<StationResponse> stationResponses = new ArrayList<>();

        for (Station station : stations) {
            stationResponses.add(StationResponse.from(station));
        }

        return ResponseEntity.status(HttpStatus.OK).body(stationResponses);
    }
}
