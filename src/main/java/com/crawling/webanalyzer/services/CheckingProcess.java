package com.crawling.webanalyzer.services;

import com.crawling.webanalyzer.models.Link;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.util.concurrent.Callable;


@Slf4j
@Data
public class CheckingProcess{

    private Link link;
    private int timeout;
    private String userAgent;

    public CheckingProcess(String href, int timeout, String userAgent) {
        this.link = new Link(href);
        this.timeout = timeout;
        this.userAgent = userAgent;
    }

    public void setLink(String href){
        this.link = new Link(href);
    }

    public Link call() throws Exception {
        Jsoup.connect(this.link.getHref())
                .userAgent(userAgent)
                .timeout(timeout)
                .method(Connection.Method.HEAD)
                .execute();
        this.link.validate();
        this.link.setComment("Success");

        return this.link;
    }
    public Link callNew() {
        try{
            Jsoup.connect(this.link.getHref())
                    .userAgent(userAgent)
                    .timeout(timeout)
                    .method(Connection.Method.HEAD)
                    .execute();
            this.link.validate();
            this.link.setComment("Success");
        }catch (Exception e){
            this.checkingProcessFaild(e);
        }
        return this.link;
    }

    public Link checkingProcessFaild (Throwable e){
            this.link.unvalidate();
            this.link.setComment(e.getMessage());

            return this.link;
    }

}
