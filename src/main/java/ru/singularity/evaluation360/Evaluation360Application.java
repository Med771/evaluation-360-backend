package ru.singularity.evaluation360;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition
public class Evaluation360Application {
    public static void main(String[] args) {
        SpringApplication.run(Evaluation360Application.class, args);
    }
}
