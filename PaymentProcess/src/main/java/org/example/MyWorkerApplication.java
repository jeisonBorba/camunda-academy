package org.example;

import jakarta.annotation.PostConstruct;
import org.example.client.MyJobHandlerClient;
import org.example.client.MyStartProcessClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MyWorkerApplication {

    private final MyJobHandlerClient client;
    private final MyStartProcessClient startProcessClient;

    public MyWorkerApplication(MyJobHandlerClient client, MyStartProcessClient startProcessClient) {
        this.client = client;
        this.startProcessClient = startProcessClient;
    }

    public static void main(String[] args) {
        SpringApplication.run(MyWorkerApplication.class, args);
    }

    @PostConstruct
    void init() {
        startProcessClient.start();
        client.connect();
    }
}
