package org.example.client;


import io.camunda.client.CamundaClient;
import io.camunda.client.api.worker.JobWorker;
import io.camunda.client.impl.CamundaClientBuilderImpl;
import org.example.handler.CreditCardServiceHandler;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class MyJobHandlerClient {

    private final CreditCardServiceHandler handler;

    public MyJobHandlerClient(CreditCardServiceHandler handler) {
        this.handler = handler;
    }

    public void connect() {
        try (final CamundaClient client = new CamundaClientBuilderImpl().build()) {

            final JobWorker creditCardWorker =
                client.newWorker()
                    .jobType("chargeCreditCard")
                    .handler(handler)
                    .timeout(Duration.ofSeconds(10).toMillis())
                    .open();

            Thread.sleep(10000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
