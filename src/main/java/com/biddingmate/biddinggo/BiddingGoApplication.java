package com.biddingmate.biddinggo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BiddingGoApplication {

    public static void main(String[] args) {
        SpringApplication.run(BiddingGoApplication.class, args);
    }

}
