package com.crawling.webanalyzer.controllers;

import com.crawling.webanalyzer.models.Link;
import com.crawling.webanalyzer.services.AnalizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RestController
public class LinksController {

    @Autowired
    private AnalizeService analizeService;

    @GetMapping("/check/links")
    public ArrayList<Link> getNbValidesLinks() throws ExecutionException, InterruptedException, TimeoutException {
        analizeService.setCheckedLinks(new ArrayList<Link>());
        return analizeService.checkLinks().get(3, TimeUnit.MINUTES);
    }


}
