package com.camunda.academy.handler;

import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.api.worker.JobClient;
import io.camunda.client.api.worker.JobHandler;

public class BudgetTrackerHandler implements JobHandler {

    @Override
    public void handle(JobClient client, ActivatedJob job) throws Exception {
        
        System.out.println("(" + job.getKey()+ ") Handling job: " + job.getType() + " - budgetCode: " + job.getVariable("budgetCode"));
        
        client.newCompleteCommand(job.getKey()).send().join();	

    }
}