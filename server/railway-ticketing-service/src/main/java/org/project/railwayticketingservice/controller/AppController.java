package org.project.railwayticketingservice.controller;

import lombok.RequiredArgsConstructor;
import org.project.railwayticketingservice.dto.app.response.HomePageResponse;
import org.project.railwayticketingservice.dto.app.response.TrainScheduleResponse;
import org.project.railwayticketingservice.service.AppService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rts/app")
@RequiredArgsConstructor
public class AppController {

    private final AppService appService;

    @GetMapping("/home")
    public ResponseEntity<HomePageResponse> home() {
        return appService.generateHomepage();
    }

    @GetMapping("/schedules/upcoming")
    public ResponseEntity<List<TrainScheduleResponse>> getUpcomingTrainSchedules() {
        return appService.getUpcomingTrainSchedules();
    }
}
