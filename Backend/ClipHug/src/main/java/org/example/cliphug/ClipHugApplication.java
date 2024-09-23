package org.example.cliphug;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ClipHugApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClipHugApplication.class, args);
    }

}
