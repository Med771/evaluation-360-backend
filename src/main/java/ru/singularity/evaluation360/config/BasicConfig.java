package ru.singularity.evaluation360.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BasicConfig {
    @Bean
    public String splitter() {
        return "!_!*!_!";
    }

    @Bean
    public Double conversionRate() {
        return 2.5;
    }
}
