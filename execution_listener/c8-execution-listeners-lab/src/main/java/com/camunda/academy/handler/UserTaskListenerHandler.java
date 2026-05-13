package com.camunda.academy.handler;

import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.api.worker.JobClient;
import io.camunda.client.api.worker.JobHandler;

public class UserTaskListenerHandler implements JobHandler {    
    @Override
    public void handle(JobClient client, ActivatedJob job) throws Exception {
        
        String teamManager = (String) job.getVariable("teamManager");

        System.out.println("(" + job.getKey()+ ") Handling job: " + job.getType() + " - teamManager: " + teamManager);
        
        client
            .newCompleteCommand(job.getKey())
            .withResult(r -> r.forUserTask()
                .correctAssignee(teamManager))
            .send()
            .join();
    }
}
