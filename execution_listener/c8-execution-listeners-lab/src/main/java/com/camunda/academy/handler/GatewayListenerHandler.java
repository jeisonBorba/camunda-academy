package com.camunda.academy.handler;

import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.api.worker.JobClient;
import io.camunda.client.api.worker.JobHandler;

public class GatewayListenerHandler implements JobHandler {
    @Override
    public void handle(JobClient client, ActivatedJob job) throws Exception {
        
        String budgetCode = (String) job.getVariable("budgetCode");

        boolean isUpdateNeeded = endsWithDifferentYear(budgetCode);

        System.out.println("(" + job.getKey()+ ") Handling job: " + job.getType() + " - isUpdateNeeded: " + isUpdateNeeded);
        
        client.newCompleteCommand(job.getKey()).variable("isUpdateNeeded", isUpdateNeeded).send().join();
    }
    /**
     * Checks if the budgetCode ends with a year different from the current year.
     * Example: "UW-ADDR-VER-2024" in 2025 returns true.
     */
    private static boolean endsWithDifferentYear(String budgetCode) {
        if (budgetCode == null || budgetCode.length() < 4) return false;
        String[] parts = budgetCode.split("-");
        if (parts.length == 0) return false;
        String lastPart = parts[parts.length - 1];
        try {
            int codeYear = Integer.parseInt(lastPart);
            int currentYear = java.time.Year.now().getValue();
            return codeYear != currentYear;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}