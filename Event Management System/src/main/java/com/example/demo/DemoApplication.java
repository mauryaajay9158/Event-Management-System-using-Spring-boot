package com.example.demo;

import com.example.demo.model.Event;
import com.example.demo.repository.EventRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {

        SpringApplication.run(DemoApplication.class, args);

    }

    @Bean
    CommandLineRunner data(EventRepository repository) {

        return args -> {

            // Add sample events only if database is empty
            if (repository.count() == 0) {

                repository.save(new Event(
                        "Music Concert",
                        LocalDate.of(2026, 9, 15),
                        "New York, NY",
                        "An evening of classical and contemporary music."
                ));

                repository.save(new Event(
                        "Art Exhibition",
                        LocalDate.of(2026, 9, 20),
                        "San Francisco, CA",
                        "Showcasing modern art from upcoming artists."
                ));

                repository.save(new Event(
                        "Technology Conference",
                        LocalDate.of(2026, 10, 5),
                        "Mumbai, India",
                        "A conference about modern technology and software development."
                ));
            }
        };
    }
}