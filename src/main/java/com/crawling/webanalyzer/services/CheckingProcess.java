package com.crawling.webanalyzer.services;

import com.crawling.webanalyzer.models.Link;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;

import java.util.concurrent.Callable;


@Slf4j
public class CheckingProcess implements Callable<Link> {

    private final Link link;
    private final int timeout;
    private final String userAgent;

    public CheckingProcess(String href, int timeout, String userAgent) {
        this.link = new Link(href);
        this.timeout = timeout;
        this.userAgent = userAgent;
    }

    @Override
    public Link call() throws Exception {
        Jsoup.connect(this.link.getHref())
                .userAgent(userAgent)
                .timeout(timeout).execute();
        this.link.validate();
        this.link.setComment("Success");

        return this.link;
    }

    public Link checkingProcessFaild (Throwable e){
            this.link.unvalidate();
            this.link.setComment(e.getMessage());

            return this.link;
    }

}
