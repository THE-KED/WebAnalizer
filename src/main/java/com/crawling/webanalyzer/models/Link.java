package com.crawling.webanalyzer.models;

import lombok.Data;

@Data
public class Link {

    private String href;
    private boolean isReacheble;
    private String comment;

    public Link(String href) {
        this.href = href;
        this.isReacheble = false;
    }
    public void validate() {
        this.isReacheble = true;
    }
    public void unvalidate() {
        this.isReacheble = false;
    }
}
