package com.crawling.webanalyzer.services;

import com.crawling.webanalyzer.models.Link;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;

import java.io.IOException;

@Slf4j
public class LinkChecking implements Runnable{

    private Link link;

    public LinkChecking(String href) {
        this.link = new Link(href);
    }

    public void setLink(String href) {
        this.link = new Link(href);
    }

    public Link getLink() {
        return link;
    }

    @Override
    public void run() {
        log.info("Checking Start");
        this.checkingProcess();
        log.info("Checking End");
    }

    private void checkingProcess (){
        try {
            Jsoup.connect(this.link.getHref()).timeout(30000).execute();
            this.link.validate();
            this.link.setComment("Success");
        } catch (IOException e) {
            this.link.unvalidate();
            this.link.setComment(e.getMessage());
        }
    }
}
