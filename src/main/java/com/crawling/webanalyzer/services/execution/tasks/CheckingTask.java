package com.crawling.webanalyzer.services.execution.tasks;

import com.crawling.webanalyzer.models.Link;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
public class CheckingTask extends RecursiveTask<List<Link>> {

    private final List<String> links;
    private final int high;
    private final int low;
    private final int max;
    private final int timeout;

    public CheckingTask (List<String> links, int high, int low, int max, int timeout) {
        this.links = links;
        this.high = high;
        this.low = low;
        this.max = max;
        this.timeout = timeout;
    }

    @Override
    protected List<Link> compute() {

        //si le nombre de lien est assez petite pour etre executer sur un thread
        if(this.links.size() <= this.max){
            List<Link> result = new ArrayList<Link>();
            ExecutorService virtualThreadPerTaskExecutor = Executors.newVirtualThreadPerTaskExecutor();
            CountDownLatch countDownLatch = new CountDownLatch(this.links.size());

            //verification des liens sur un thread virtuel' en parallèle sur un thread virtuel par lien'
            for(String href:this.links){
                Link link = new Link(href);
                CompletableFuture.supplyAsync(()->{
                    try {
                        Jsoup.connect(link.getHref())
                                .userAgent("Chrome/91.0.4472.124")
                                .timeout(this.timeout).execute();
                        link.validate();
                        link.setComment("Success");

                    }catch (Exception e){
                        link.unvalidate();
                        link.setComment(e.getMessage());
                    }
                    return link;
                }, virtualThreadPerTaskExecutor)
                        .thenAccept(response ->{
                            result.add(response);
                            countDownLatch.countDown();
                        });

            }

            //attente que tous les liens aient ete verifie' afin de terminer la tache recursive'
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info("Recursive done");
            return result;
        }else {
            //si le nombre de lien est trop grand pour etre executer sur un thread

            int mid = (low + high) / 2;
            List<String> leftList = links.subList(low, mid);
            List<String> rightList = links.subList(mid,high);
            CheckingTask leftTask = new CheckingTask(leftList,leftList.size()-1,0,max,timeout);
            CheckingTask rightTask = new CheckingTask(rightList,rightList.size()-1,0,max,timeout);
            leftTask.fork();
            List<Link> rightResult = rightTask.compute();
            List<Link> leftResult = leftTask.join();
            List<Link> result = new ArrayList<Link>();
            result.addAll(leftResult);
            result.addAll(rightResult);

            return result;

        }

    }
}
