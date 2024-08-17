package com.crawling.webanalyzer.services;

import com.crawling.webanalyzer.models.Link;
import com.crawling.webanalyzer.models.PageInfos;
import com.crawling.webanalyzer.services.domains.DomainLinkMapper;
import com.crawling.webanalyzer.services.execution.tasks.CheckingTaskV2;
import com.crawling.webanalyzer.services.scrapper.*;
import lombok.Data;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

@Service
@SessionScope
@Data
public class AnalizeServiceV3 {

    @Autowired
    private ForkJoinPool forkJoinPool;

    @Value("${maxLinkByThreads}")
    private int maxLinkByThreads;
    @Value("${requestTimeout}")
    private int timeout;
    @Value("${userAgent}")
    private String userAgent;

    private static final Logger log = LoggerFactory.getLogger(AnalizeServiceV3.class);
    private PageInfos currentPageInfos;
    private Document currentPage;
    private String currentUrl;
    private List<String> links;
    private ArrayList<Link> checkedLinks = new ArrayList<>();

    private void init(){
        this.currentPageInfos=null;
        this.currentPage=null;
        this.currentUrl=null;
    }

    //recupere le document Html de la page depuis l'url
    public void loadPage(String url) throws IOException {
        this.init();
        this.currentPage = Extractor.Extract(url);
        this.currentUrl = url;
    }

    //recupere les informations de la page (titre, version html, nombre de rubriques, presence d'un formulaire d'authentification)
    public void loadInfos(){
        this.currentPageInfos = new PageInfos();
        this.currentPageInfos.setPageTitle(TitleDetector.detect(this.currentPage));
        this.currentPageInfos.setHtmlVersion(VersionDetector.detect(this.currentPage));
        this.currentPageInfos.setRubriquesNumber(RubriqueDetector.detect(this.currentPage));
        this.currentPageInfos.setHadAuthForm(AuthFormDetector.detect(this.currentPage));
        this.currentPageInfos.setLinks(LinksDetector.detect(this.currentPage));
        this.currentPageInfos.setLinksByDomain(DomainLinkMapper.map(this.currentPageInfos.getLinks()));
        this.links = this.currentPageInfos.getLinks();

    }

    //validations des liens de la page.
    @Async
    public CompletableFuture<ArrayList<Link>> checkLinks(){

        CheckingTaskV2 checkingTask = new CheckingTaskV2(
                links,links.size()-1,0,this.maxLinkByThreads,timeout,userAgent);
        checkedLinks = (ArrayList<Link>) this.forkJoinPool.invoke(checkingTask);

        return CompletableFuture.completedFuture(checkedLinks);
    }

}
