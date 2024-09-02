package com.crawling.webanalyzer.services;

import com.crawling.webanalyzer.models.Link;
import com.crawling.webanalyzer.models.PageInfos;
import com.crawling.webanalyzer.services.domains.DomainLinkMapper;
import com.crawling.webanalyzer.services.scrapper.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
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
@Slf4j
@Data
public class AnalizeService {

    private final ExecutorService virtualThreadPool;
    private final LinkCheckingService linkCheckingService;
    private PageInfos currentPageInfos;
    private Document currentPage;
    private List<String> links;
    private ArrayList<Link> checkedLinks = new ArrayList<>();

    @Autowired
    public AnalizeService(ExecutorService virtualThreadPool, LinkCheckingService linkCheckingService){
        this.virtualThreadPool = virtualThreadPool;
        this.linkCheckingService = linkCheckingService;
    }

    private void init(){
        this.currentPageInfos=null;
        this.currentPage=null;
        this.currentPageInfos = new PageInfos();
        this.currentPageInfos.setLinks(new ArrayList<String>());
    }

    //recupere le document Html de la page depuis l'url
    public void loadPage(String url) throws IOException {
        this.init();
        this.currentPage = Extractor.Extract(url);
    }

    //recupere les informations de la page (titre, version html, nombre de rubriques, presence d'un formulaire d'authentification)
    public void loadInfos(){
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
    public CompletableFuture<List<Link>> checkLinks(){

        var checkingProcess = links.stream()
                .map(Link::new)
                .map(link -> CompletableFuture.supplyAsync(
                        ()->linkCheckingService.check(link),virtualThreadPool)
                ).toList();

        CompletableFuture<List<Link>> listCompletableFuture = CompletableFuture.allOf(
                    checkingProcess.toArray(new CompletableFuture[0])
                ).thenApply(_ -> checkingProcess.stream().map(CompletableFuture::join)
                .toList());
        listCompletableFuture.thenAccept(result -> this.checkedLinks.addAll(result));

        return listCompletableFuture.thenApply(_ -> this.checkedLinks);
    }

}
