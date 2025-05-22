package com.linuxdo.cdkdistribution;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CdkDistributionApplication {

    public static void main(String[] args) {
        SpringApplication.run(CdkDistributionApplication.class, args);
    }
}
