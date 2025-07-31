package org.project.railwayticketingservice.entity;

import lombok.*;

import java.time.LocalDateTime;
import java.time.Month;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Time {
    int year;
    String month;
    int day;
    int hour;
    int minute;

    public LocalDateTime getLocalDateTime() {
        return LocalDateTime.of(year, Month.valueOf(month), day, hour, minute);
    }

    public static Time fromLocalDateTime(LocalDateTime localDateTime) {
        return Time.builder()
                .year(localDateTime.getYear())
                .month(localDateTime.getMonth().toString())
                .day(localDateTime.getDayOfMonth())
                .hour(localDateTime.getHour())
                .minute(localDateTime.getMinute())
                .build();
    }
}
