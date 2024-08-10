package com.crawling.webanalyzer.services.execution;

public class CustomExecutorImpl implements CustomExecutor{

    @Override
    public void execute(Runnable command) {

        command.run();
    }
}
