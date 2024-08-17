package com.crawling.webanalyzer.services;

import com.crawling.webanalyzer.models.Link;
import com.crawling.webanalyzer.models.PageInfos;
import com.crawling.webanalyzer.services.domains.DomainLinkMapper;
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
import java.util.concurrent.ExecutorService;

@Service
@SessionScope
@Data
public class AnalizeServiceV2 {

    @Autowired
    private ExecutorService virtualThreadPool;
    @Value("${maxLinkByThreads}")
    private int maxLinkByThreads;
    @Value("${requestTimeout}")
    private int timeout;
    @Value("${userAgent}")
    private String userAgent;

    private static final Logger log = LoggerFactory.getLogger(AnalizeServiceV2.class);
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
    public CompletableFuture<ArrayList<Link>> checkLinks() throws InterruptedException {
        List<CompletableFuture<Link>> futures = new ArrayList<CompletableFuture<Link>>();
        for(String link : links){
            CheckingProcess processV2 = new CheckingProcess(link,timeout,userAgent);
            CompletableFuture<Link> future = CompletableFuture.supplyAsync(()->{
                try {
                    return  processV2.call();
                } catch (Exception e) {
                    processV2.checkingProcessFaild(e);
                    return processV2.getLink();
                }
            },virtualThreadPool);
            futures.add(future);
        }
        CompletableFuture<List<Link>> result = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                        .thenApply(_ -> futures.stream()
                        .map(CompletableFuture::join)
                        .toList());
        result.thenAccept(results -> this.checkedLinks.addAll(results));

        return result.thenApply(_ -> this.checkedLinks);

    }

}
