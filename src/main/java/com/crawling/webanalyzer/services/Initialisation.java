package com.crawling.webanalyzer.services;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class Initialisation {

    @Bean("virtualThreadPool")
    ExecutorService virtualThreadPool(){
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
