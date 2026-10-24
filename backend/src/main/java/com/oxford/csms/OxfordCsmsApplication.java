package com.oxford.csms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class OxfordCsmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(OxfordCsmsApplication.class, args);
    }
}
