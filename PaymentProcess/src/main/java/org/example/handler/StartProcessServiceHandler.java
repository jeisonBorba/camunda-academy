package org.example.handler;

import io.camunda.client.CamundaClient;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class StartProcessServiceHandler {

    private final CamundaClient client;

    public StartProcessServiceHandler(CamundaClient client) {
        this.client = client;
    }

    public void handle(Map<String, Object> variables) throws Exception {
        client.newCreateInstanceCommand()
            .bpmnProcessId("paymentProcess")
            .latestVersion()
            .variables(variables)
            .send()
            .join();
    }
}
