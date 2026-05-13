package com.camunda.academy.handler;

import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.api.worker.JobClient;
import io.camunda.client.api.worker.JobHandler;

public class SearchCustomerHandler implements JobHandler {

    @Override
    public void handle(JobClient client, ActivatedJob job) throws Exception {
        
        System.out.println("(" + job.getKey()+ ") Handling job: " + job.getType());

        // CMaps REST API call
        
        client.newCompleteCommand(job.getKey()).send().join();

    }
}