package dev.raffadevs.frota;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class FrotaApplication {

    public static void main(String[] args) {
        SpringApplication.run(FrotaApplication.class, args);
    }
}
