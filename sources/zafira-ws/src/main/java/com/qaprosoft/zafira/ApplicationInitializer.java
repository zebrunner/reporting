package com.qaprosoft.zafira;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class ApplicationInitializer {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationInitializer.class, args);
    }

}
