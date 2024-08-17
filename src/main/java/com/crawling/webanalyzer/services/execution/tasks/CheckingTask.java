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
        if((high - low) <= max){
            List<CompletableFuture<Link>> futures = new ArrayList<>();

            //verification des liens sur des threads virtuels
            for(int i = low; i < high; i++){
                CheckingProcess checkingProcess = new CheckingProcess(links.get(i),timeout,userAgent);
                CompletableFuture<Link> future = CompletableFuture.supplyAsync(()->{
                    try {
                        return checkingProcess.call();
                    } catch (Exception e) {
                        checkingProcess.checkingProcessFaild(e);
                        return checkingProcess.getLink();
                    }
                }, virtualThreadPool);
                futures.add(future);
            }
            log.info("Recursive done");
            return futures;
        }else {
            //si le nombre de lien est trop grand pour etre executer sur un thread

            int mid = (low + high) / 2;
            CheckingTask leftTask = new CheckingTask(links,mid,low,max,timeout,userAgent,virtualThreadPool);
            CheckingTask rightTask = new CheckingTask(links,high,mid,max,timeout,userAgent,virtualThreadPool);
            leftTask.fork();
            List<CompletableFuture<Link>> futures = new ArrayList<>();
            futures.addAll(rightTask.compute());
            futures.addAll(leftTask.join());

            return futures;

        }

    }
}
