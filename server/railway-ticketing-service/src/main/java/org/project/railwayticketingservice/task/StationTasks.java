package org.project.railwayticketingservice.task;

import java.util.List;

import org.project.railwayticketingservice.entity.Station;
import org.project.railwayticketingservice.repository.StationRepository;
import org.project.railwayticketingservice.util.Utilities;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class StationTasks {

    private final StationRepository stationRepository;
    private final Utilities utilities;

    public StationTasks(StationRepository stationRepository, Utilities utilities) {
        this.stationRepository = stationRepository;
        this.utilities = utilities;
    }

    @Transactional
    public void stationPopulater() {
        log.info("*** Initial Station Population started ***");
        /* *
         * HOW?
         * - read from an external file, probably via Files
         * - compose the station instance and store in db
         *
         * - file should consist of station name, code, LGA and isActive in csv format
         * - each line should consist of a different entry
         * */

        // reading file
        String filePath = "./src/main/resources/station-list.csv";
        List<String> data = utilities.fileReader(filePath, "station");

        // generating entities and persisting
        for (String datum : data) {
            String[] splitData = datum.split(",");
            String name = splitData[0];
            String code = splitData[1];
            String LGA = splitData[2];
            boolean isActive = Boolean.parseBoolean(splitData[3]);

            try {
                log.info("Generating station entity for {}", name);
                Station station = Station.builder()
                        .LGA(LGA)
                        .code(code)
                        .name(name)
                        .isActive(isActive)
                        .build();

                log.info("Generated station entity for {}", name);
                stationRepository.save(station);
                log.info("Persisted station entity for {}", name);
            } catch (Exception e) {
                log.error("Failed to create Station entity for station: {}\n See more: {}", name, e.getMessage());
            }
        }

        log.info("Successfully populated station relation");
        log.info("*** Initial Station Population completed ***");

    }
}
