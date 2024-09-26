package com.zedeck.smartoutletserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableScheduling
public class SmartOutletServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartOutletServerApplication.class, args);
    }

}
