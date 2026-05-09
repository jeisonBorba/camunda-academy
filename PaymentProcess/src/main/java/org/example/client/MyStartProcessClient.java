package org.example.client;

import org.example.handler.StartProcessServiceHandler;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class MyStartProcessClient {

    private final StartProcessServiceHandler handler;

    public MyStartProcessClient(StartProcessServiceHandler handler) {
        this.handler = handler;
    }

    public void start() {
        final var variables = new HashMap<String, Object>();
        variables.put("reference", "C8_12345");
        variables.put("amount", 100.00);
        variables.put("cardNumber", "1234567812345678");
        variables.put("cardExpiry", "12/2027");
        variables.put("cardCVC", "123");

        try {
            handler.handle(variables);
        } catch (Exception ex) {
            System.out.println(String.format("Error while starting the process: s%", ex.getMessage()));
        }
    }
}
