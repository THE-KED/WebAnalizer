package com.crawling.webanalyzer.services;

import com.crawling.webanalyzer.models.Link;
import com.crawling.webanalyzer.models.PageInfos;
import com.crawling.webanalyzer.services.scrapper.*;
import lombok.Data;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(AnalizeService.class);
    private PageInfos currentPageInfos;
    private Document currentPage;
    private String currentUrl;
    private List<String>[] links;
    private ArrayList<Link> checkedLinks = new ArrayList<Link>();

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
    public PageInfos loadInfos() throws MalformedURLException {
        this.currentPageInfos = new PageInfos();
        this.currentPageInfos.setPageTitle(TitleDetector.detect(this.currentPage));
        this.currentPageInfos.setHtmlVersion(VersionDetector.detect(this.currentPage));
        this.currentPageInfos.setRubriquesNumber(RubriqueDetector.detect(this.currentPage));
        this.currentPageInfos.setHadAuthForm(AuthFormDetector.detect(this.currentPage));

        String domain = new URL(this.currentUrl).getAuthority();

        this.links = LinksDetector.detect(this.currentPage, domain);
        return this.currentPageInfos;
    }



    //validations des liens de la page.
    @Async
    public CompletableFuture<ArrayList<Link>> checkLinks() throws ExecutionException, InterruptedException {
        ArrayList<String> allLinks = new ArrayList<String>();
        allLinks.addAll(this.links[0]);
        allLinks.addAll(this.links[1]);
//        List<Future<LinkChecking>> checkeds = new ArrayList<>();
        CountDownLatch uncheckeds = new CountDownLatch(allLinks.size());
//        log.info("Checking starting :{}",uncheckeds.getCount());

        //creations du thread pool
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

        //soumissions des tache au threadPool
//        int index = 1;
        for(String link :allLinks){
//            log.info("link N{} :", index);
            Link lien = new Link(link);
//            LinkChecking traitement = new LinkChecking(link);
            CompletableFuture.supplyAsync(()->{
                try {
                    Jsoup.connect(lien.getHref())
                            .userAgent("Chrome/91.0.4472.124")
                            .timeout(30000).execute();
                    lien.validate();
                    lien.setComment("Success");
                } catch (Exception e) {
                    lien.unvalidate();
                    lien.setComment(e.getMessage());
                }
                return lien;
            },cachedThreadPool).thenAccept(respons -> {
                this.checkedLinks.add(respons);
                log.info("Checking End {}/{}",checkedLinks.size(),allLinks.size());
                uncheckeds.countDown();
            });
//            log.info("Checking End {}/{}",checkedLinks.size(),allLinks.size());

//            Future<LinkChecking> checked = cachedThreadPool.submit(traitement,traitement);?
//            checkeds.add(checked);
//            index++;
        }

        uncheckeds.await();
        log.info("Checked end {}/{}",this.checkedLinks.size(),allLinks.size());

        //attente de la fin des taches et recuperation des resultats
//        for(Future<LinkChecking> checked :checkeds){
//            try {
//                LinkChecking completed = checked.get();
//                this.checkedLinks.add(completed.getLink());
//            } catch (ExecutionException | InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }

        log.info("Completed {}", checkedLinks.size());

        return CompletableFuture.completedFuture(checkedLinks);
    }


}
