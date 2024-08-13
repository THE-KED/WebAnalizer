package com.crawling.webanalyzer.services.execution.tasks;

import com.crawling.webanalyzer.models.Link;
import lombok.Data;

import java.util.concurrent.CompletableFuture;

@Data
public class LinkChecking {

    private Link link;
    private CompletableFuture<Link> future;
}
