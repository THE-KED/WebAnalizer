package com.crawling.webanalyzer.services;

import com.crawling.webanalyzer.models.Link;
import com.crawling.webanalyzer.models.PageInfos;
import com.crawling.webanalyzer.services.execution.tasks.CheckingTask;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Service
@SessionScope
@Data
public class AnalizeService {

    @Autowired
    private ForkJoinPool forkJoinPool;
    @Autowired
    private ExecutorService virtualThreadPool;
    @Value("${maxLinkByThreads}")
    private int maxLinkByThreads;
    @Value("${requestTimeout}")
    private int timeout;
    @Value("${userAgent}")
    private String userAgent;

    private static final Logger log = LoggerFactory.getLogger(AnalizeService.class);
    private PageInfos currentPageInfos;
    private Document currentPage;
    private String currentUrl;
    private List<String>[] links;
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
    public void loadInfos() throws MalformedURLException {
        this.currentPageInfos = new PageInfos();
        this.currentPageInfos.setPageTitle(TitleDetector.detect(this.currentPage));
        this.currentPageInfos.setHtmlVersion(VersionDetector.detect(this.currentPage));
        this.currentPageInfos.setRubriquesNumber(RubriqueDetector.detect(this.currentPage));
        this.currentPageInfos.setHadAuthForm(AuthFormDetector.detect(this.currentPage));

        String domain = new URL(this.currentUrl).getAuthority();

        this.links = LinksDetector.detect(this.currentPage, domain);
    }



    //validations des liens de la page.
    @Async
    public CompletableFuture<ArrayList<Link>> checkLinks(){
        ArrayList<String> allLinks = new ArrayList<>();
        allLinks.addAll(this.links[0]);
        allLinks.addAll(this.links[1]);

        CheckingTask checkingTask = new CheckingTask(
                allLinks,allLinks.size()-1,0,this.maxLinkByThreads,timeout,userAgent,this.virtualThreadPool);
        List<CompletableFuture<Link>> futures = this.forkJoinPool.invoke(checkingTask);

        // Collecte des résultats de manière non bloquante
        CompletableFuture<List<Link>> allResultsFuture = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(_ -> futures.stream()
                        .map(CompletableFuture::join)
                        .toList());
        allResultsFuture.thenAccept(results -> this.checkedLinks.addAll(results));

        return allResultsFuture.thenApply(_ -> this.checkedLinks);
    }


}
