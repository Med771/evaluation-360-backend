package ru.singularity.evaluation360;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@OpenAPIDefinition
@EnableAsync
@EnableScheduling
public class Evaluation360Application {
    public static void main(String[] args) {
        SpringApplication.run(Evaluation360Application.class, args);
    }
}
