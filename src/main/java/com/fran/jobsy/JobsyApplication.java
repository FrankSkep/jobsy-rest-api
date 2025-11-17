package com.fran.jobsy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableAsync
@EnableCaching
public class JobsyApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobsyApplication.class, args);
//        testFunctionalInterface();
    }

    public static void testFunctionalInterface() {
        Consumer<String> impr = s -> System.out.print("Hola -> " + s);
        Consumer<String> length = s -> System.out.println(" | Largo: " + s.length());

        Consumer<String> pipeline = impr.andThen(length);

        var names = Arrays.asList("Frank", "Alan");

        names = names
                .stream()
                .map(String::toUpperCase)
                .toList();

        names.forEach(pipeline);
    }
}