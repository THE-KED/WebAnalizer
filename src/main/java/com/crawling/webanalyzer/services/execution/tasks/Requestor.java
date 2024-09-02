package com.crawling.webanalyzer.services.execution.tasks;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class Requestor {

    @Value("${requestTimeout}")
    private int timeout;

    @Value("${userAgent}")
    private String userAgent;

    public void request(String url) throws IOException {
        Jsoup.connect(url)
                .userAgent(userAgent)
                .timeout(timeout)
                .method(Connection.Method.HEAD)
                .execute();
    }
}
