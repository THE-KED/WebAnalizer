package com.crawling.webanalyzer.models;

import lombok.Data;

import java.util.List;

@Data
public class PageInfos {

    private String htmlVersion;
    private String pageTitle;
    private int[][] rubriquesNumber;
    private List<Link>[] links;
    private boolean hadAuthForm;
}
