package org.example.functions;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class FunctionApp {
    private static final AtomicLong ID = new AtomicLong();

    public static void main(String[] args) {
        SpringApplication.run(FunctionApp.class, args);
    }

    @Bean
    public Function<String, String> producer() {
        return timerInfo -> String.format("Hello World #%d, @%s", ID.incrementAndGet(), Instant.now());
    }

    @Bean
    public Function<String, String> receiver() {
        return json -> json;
    }
}
