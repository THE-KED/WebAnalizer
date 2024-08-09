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
    public CompletableFuture<ArrayList<Link>> checkLinks() {
        ArrayList<ArrayList<String>> linksLots = new ArrayList<ArrayList<String>>();
        ArrayList<String> allLinks = new ArrayList<String>();
        allLinks.addAll(this.links[0]);
        allLinks.addAll(this.links[1]);
        List<Future<LinkChecking>> checkeds = new ArrayList<>();

//        //decoupage de la liste des liens de la page en lot de 10 liens.
//        int lastLotSize = allLinks.size()%10;
//        for(int i = 0;i<allLinks.size()-lastLotSize;i+=10){
//            ArrayList<String> tempList = new ArrayList<String>(allLinks.subList(i, i + 10));
//            linksLots.add(tempList);
//        }
//        if (lastLotSize > 0){
//            ArrayList<String> tempList = new ArrayList<String>(allLinks.subList((allLinks.size()-(lastLotSize + 1)),allLinks.size()));
//            linksLots.add(tempList);
//        }
//        int i=0;
//
//        //creation et lancement des threads pour tester chacun un lot de liens.
//        for (ArrayList<String> lot : linksLots) {
//            i++;
//          Runnable traitement = new Runnable() {
//              @Override
//              public void run() {
//                  log.info("start");
//                  for(String href : lot){
//                      Link link = new Link(href);
//                      try {
//                          Jsoup.connect(link.getHref()).get();
//                          link.validate();
//                          link.setComment("Success");
//                      } catch (IOException e) {
//                          link.unvalidate();
//                          link.setComment(e.getMessage());
//                      }
//                    checkedLinks.add(link);
//                  }
//                  log.info("Success");
//              }
//          };
//          Thread thread = new Thread(traitement,"Thread "+i);
//          log.info(thread.getName());
//          thread.start();
//        }

        //creations du thread pool
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

        //soumissions des tache au threadPool
        int index = 1;
        for(String link :allLinks){
            log.info("link N"+index+" :");
            LinkChecking traitement = new LinkChecking(link);
            Future<LinkChecking> checked = cachedThreadPool.submit(traitement,traitement);
            checkeds.add(checked);
            index++;
        }

        //attente de la fin des taches et recuperation des resultats
        for(Future<LinkChecking> checked :checkeds){
            try {
                LinkChecking completed = checked.get();
                this.checkedLinks.add(completed.getLink());
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }



//        //attente que tous les threads se terminent.
//        while (checkedLinks.size() < allLinks.size()) {
//            log.info("Checked "+checkedLinks.size()+" links");
//            try {
//                Thread.sleep(200);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
        log.info("Completed "+checkedLinks.size());

        return CompletableFuture.completedFuture(checkedLinks);
    }


}
