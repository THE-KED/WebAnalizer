package com.crawling.webanalyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.IOException;

@SpringBootApplication
@EnableAsync
public class WebAnalyzerApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(WebAnalyzerApplication.class, args);
	}

}
