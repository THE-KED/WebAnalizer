package com.crawling.webanalyzer.services;

import com.crawling.webanalyzer.models.Link;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.concurrent.Callable;


@Slf4j
@Data
public class CheckingProcessV2 implements Runnable {

    private Link link;
    private int timeout;
    private String userAgent;

    public CheckingProcessV2(String href, int timeout, String userAgent) {
        this.link = new Link(href);
        this.timeout = timeout;
        this.userAgent = userAgent;
    }

    public void setLink(String href){
        this.link = new Link(href);
    }

    @Override
    public void run() {
        try {
            Jsoup.connect(this.link.getHref())
                    .userAgent(userAgent)
                    .timeout(timeout)
                    .method(Connection.Method.HEAD)
                    .execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.link.validate();
        this.link.setComment("Success");

    }

    public Link checkingProcessFaild (Throwable e){
            this.link.unvalidate();
            this.link.setComment(e.getMessage());

            return this.link;
    }

}
