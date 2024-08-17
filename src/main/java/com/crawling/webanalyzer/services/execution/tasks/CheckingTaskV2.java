package com.crawling.webanalyzer.services.execution.tasks;

import com.crawling.webanalyzer.models.Link;
import com.crawling.webanalyzer.services.CheckingProcess;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RecursiveTask;

@Slf4j
@AllArgsConstructor
public class CheckingTaskV2 extends RecursiveTask<List<Link>> {

    private final List<String> links;
    private final int high;
    private final int low;
    private final int max;
    private int timeout;
    private String userAgent;


    @Override
    protected List<Link> compute() {

        // Si le nombre de liens est assez petit pour être exécuté sur un seul thread
        if ((high - low) <= max) {
            List<Link> result = new ArrayList<>();
            for (int i = low; i < high; i++) {
                CheckingProcess checkingProcess = new CheckingProcess(links.get(i), timeout, userAgent);
                try {
                    result.add(checkingProcess.call());
                } catch (Exception e) {
                    checkingProcess.checkingProcessFaild(e);
                    result.add(checkingProcess.getLink());
                }
            }
            return result;

        } else {
            int mid = (low + high) / 2;

            CheckingTaskV2 leftTask = new CheckingTaskV2(links, mid, low, max, timeout, userAgent);
            CheckingTaskV2 rightTask = new CheckingTaskV2(links, high, mid, max, timeout, userAgent);
            leftTask.fork();
            List<Link> rightResult = rightTask.compute();
            List<Link> leftResult = leftTask.join();
            rightResult.addAll(leftResult);

            return rightResult;
        }

    }
}
