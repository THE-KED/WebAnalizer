package com.crawling.webanalyzer.services.execution.tasks;

import com.crawling.webanalyzer.models.Link;
import com.crawling.webanalyzer.services.CheckingProcess;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
@AllArgsConstructor
public class CheckingTask extends RecursiveTask<List<CompletableFuture<Link>>> {

    private final List<String> links;
    private final int high;
    private final int low;
    private final int max;
    private int timeout;
    private String userAgent;
    private final ExecutorService virtualThreadPool;


    @Override
    protected List<CompletableFuture<Link>> compute() {

        //si le nombre de lien est assez petite pour etre executer sur un thread
        if(this.links.size() <= this.max){
            List<CompletableFuture<Link>> futures = new ArrayList<>();

            //verification des liens sur un thread virtuel' en parallèle sur un thread virtuel par lien'
            for(String href:this.links){
                CheckingProcess checkingProcess = new CheckingProcess(href,timeout,userAgent);
                CompletableFuture<Link> future = CompletableFuture.supplyAsync(()->{
                    try {
                        return checkingProcess.call();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }, virtualThreadPool).exceptionally(checkingProcess::checkingProcessFaild);
                futures.add(future);
            }
            log.info("Recursive done");
            return futures;
        }else {
            //si le nombre de lien est trop grand pour etre executer sur un thread

            int mid = (low + high) / 2;
            List<String> leftList = links.subList(low, mid);
            List<String> rightList = links.subList(mid,high);
            CheckingTask leftTask = new CheckingTask(leftList,leftList.size()-1,0,max,timeout,userAgent,virtualThreadPool);
            CheckingTask rightTask = new CheckingTask(rightList,rightList.size()-1,0,max,timeout,userAgent,virtualThreadPool);
            leftTask.fork();
            List<CompletableFuture<Link>> futures = new ArrayList<>();
            futures.addAll(rightTask.compute());
            futures.addAll(leftTask.join());

            return futures;

        }

    }
}
