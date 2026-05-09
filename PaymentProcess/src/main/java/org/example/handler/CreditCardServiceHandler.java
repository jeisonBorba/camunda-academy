package org.example.handler;

import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.api.worker.JobClient;
import io.camunda.client.api.worker.JobHandler;
import org.example.service.CreditCardService;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class CreditCardServiceHandler implements JobHandler {

    private final CreditCardService creditCardService;

    public CreditCardServiceHandler(CreditCardService creditCardService) {
        this.creditCardService = creditCardService;
    }

    @Override
    public void handle(JobClient client, ActivatedJob job) {

        final var inputVariables = job.getVariablesAsMap();
        final String reference = (String) inputVariables.get("reference");
        final Double amount = (Double) inputVariables.get("amount");
        final String cardNumber = (String) inputVariables.get("cardNumber");
        final String cardExpiry = (String) inputVariables.get("cardExpiry");
        final String cardCVC =  (String) inputVariables.get("cardCVC");

        final var confirmation = creditCardService.chargeCreditCard(reference, amount, cardNumber, cardExpiry, cardCVC);

        final var outputVariables = new HashMap<String, Object>();
        outputVariables.put("confirmation", confirmation);

        client.newCompleteCommand(job.getKey())
            .variables(outputVariables)
            .send()
            .join();
    }
}

