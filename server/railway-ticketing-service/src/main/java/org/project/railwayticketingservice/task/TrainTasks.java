package org.project.railwayticketingservice.task;

import java.util.List;

import org.project.railwayticketingservice.entity.Train;
import org.project.railwayticketingservice.repository.TrainRepository;
import org.project.railwayticketingservice.util.Utilities;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TrainTasks {

    private final TrainRepository trainRepository;
    private final Utilities utilities;

    public TrainTasks(TrainRepository trainRepository, Utilities utilities) {
        this.trainRepository = trainRepository;
        this.utilities = utilities;
    }

    /* task to populate trains */
    @Transactional
    public void trainPopulater() {
        log.info("*** Initial train population ***");
        /* *
         * HOW?
         * - read from an external file, probably via Files
         * - compose the train instance and store in db
         *
         * - file should consist of station name, code, LGA and isActive in csv format
         * - each line should consist of a different entry
         * - list returned from the reader should consist of the code name;
         * */

        // reading file
        String filePath = "./src/main/resources/station-list.csv";
        List<String> data = utilities.fileReader(filePath, "train");

        // generating entities and persisting
        for (String datum : data) {

            try {
                log.info("Generating train entity for {}", datum);
                Train station = Train.builder()
                        .capacity(1040L)
                        .name(datum)
                        .isActive(true)
                        .build();

                log.info("Generated train entity for {}", datum);
                trainRepository.save(station);
                log.info("Persisted train entity for {}", datum);
            } catch (Exception e) {
                log.error("Failed to create train entity for station: {}\n See more: {}", datum, e.getMessage());
            }
        }

        log.info("Successfully populated train relation");
        log.info("*** Initial Train Population completed ***");
    }
}
