package com.crawling.webanalyzer.services;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

@Configuration
public class Initialisation {

    // Initialisation des services et autres objets nécessaires à l'application
    @Bean("forkJoinPool")
    ForkJoinPool pool(){
        return new ForkJoinPool();
    }

    @Bean("virtualThreadPool")
    ExecutorService virtualThreadPool(){
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
