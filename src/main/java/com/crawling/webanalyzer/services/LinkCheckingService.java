package com.crawling.webanalyzer.services;

import com.crawling.webanalyzer.models.Link;
import com.crawling.webanalyzer.services.execution.tasks.Requestor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class LinkCheckingService {

    private final Requestor requestor;

    @Autowired
    public LinkCheckingService(Requestor requestor) {
        this.requestor = requestor;
    }

    public Link check(Link link){

        try {
            this.requestor.request(link.getHref());
            link.validate();
            link.setComment("Success");
        }catch (IOException e){
            this.checkingProcessFaild(e, link);
        }
        return link;
    }

    private void checkingProcessFaild (IOException e,Link link){
        link.unvalidate();
        link.setComment(e.getMessage());
    }

}
