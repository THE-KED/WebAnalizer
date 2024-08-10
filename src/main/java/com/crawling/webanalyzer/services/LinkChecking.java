package com.crawling.webanalyzer.services;

import com.crawling.webanalyzer.models.Link;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;


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
            Jsoup.connect(this.link.getHref())
                    .userAgent("Chrome/91.0.4472.124")
                    .timeout(30000).execute();
            this.link.validate();
            this.link.setComment("Success");
        } catch (Exception e) {
            this.link.unvalidate();
            this.link.setComment(e.getMessage());
        }
    }
}
