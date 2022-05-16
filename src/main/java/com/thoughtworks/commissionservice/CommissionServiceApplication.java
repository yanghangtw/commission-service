package com.thoughtworks.commissionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.*;
import java.time.chrono.Chronology;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Calendar;

@SpringBootApplication
public class CommissionServiceApplication {

    public static void main(String[] args) {
//        SpringApplication.run(CommissionServiceApplication.class, args);
        Instant i1 = Instant.now();
        ZonedDateTime t1 = ZonedDateTime.ofInstant(i1, ZoneId.of("UTC+07:00"));
        ZonedDateTime t2 = ZonedDateTime.ofInstant(i1, ZoneId.of("Asia/Shanghai"));
        System.out.println(t2);
        System.out.println(t1.equals(t2));
        System.out.println(t1.toLocalDateTime());
        System.out.println(t2.toLocalDateTime());
        System.out.println(t1.toInstant().equals(t2.toInstant()));

        System.out.println(Chronology.getAvailableChronologies());

        Chronology japanese = Chronology.of("Japanese");
        System.out.println(japanese.zonedDateTime(i1, ZoneId.of("UTC+09:00")));

        System.out.println(ZoneId.of("Europe/Berlin").getRules().getOffset(LocalDateTime.now()));

        System.out.println(LocalDateTime.now(Clock.system(ZoneId.of("UTC+07:00"))));
    }

}
