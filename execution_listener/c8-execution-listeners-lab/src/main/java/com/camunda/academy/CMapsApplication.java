package com.camunda.academy;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Properties;
import java.util.Scanner;

import com.camunda.academy.handler.UserTaskListenerHandler;
import com.camunda.academy.handler.BudgetTrackerHandler;
import com.camunda.academy.handler.GatewayListenerHandler;
import com.camunda.academy.handler.SearchCustomerHandler;

import io.camunda.client.CamundaClient;
import io.camunda.client.api.worker.JobWorker;
import io.camunda.client.impl.oauth.OAuthCredentialsProvider;
import io.camunda.client.impl.oauth.OAuthCredentialsProviderBuilder;

public class CMapsApplication {

    // Zeebe Client Credentials
    private static final String CAMUNDA_PROPERTIES_PATH = "src/main/resources/application.properties";
    private static String CAMUNDA_AUTHORIZATION_SERVER_URL;
    private static String CAMUNDA_CLIENT_ID;
    private static String CAMUNDA_CLIENT_SECRET;
    private static String CAMUNDA_TOKEN_AUDIENCE;
    private static String CAMUNDA_REST_ADDRESS;
    private static String CAMUNDA_GRPC_ADDRESS;
    public static void main(String[] args) {
        loadProperties();

        final OAuthCredentialsProvider credentialsProvider = new OAuthCredentialsProviderBuilder()
            .authorizationServerUrl(CAMUNDA_AUTHORIZATION_SERVER_URL)
            .audience(CAMUNDA_TOKEN_AUDIENCE)
            .clientId(CAMUNDA_CLIENT_ID)
            .clientSecret(CAMUNDA_CLIENT_SECRET)
            .build();

        try (final CamundaClient  client = CamundaClient.newClientBuilder()
                .grpcAddress(URI.create(CAMUNDA_GRPC_ADDRESS))
                .restAddress(URI.create(CAMUNDA_REST_ADDRESS))
                .credentialsProvider(credentialsProvider)
                 .build()) {
            
            System.out.println("Connected to: " + client.newTopologyRequest().send().join());

            // Start a Job Worker
            final JobWorker SearchCustomerWorker = client.newWorker()
                .jobType("verifyAddress")
                .handler(new SearchCustomerHandler())
                .open();	
                            
            final JobWorker BudgetTrackerWorker = client.newWorker()
                .jobType("trackBudgetCode")
                .handler(new BudgetTrackerHandler())
                .open();

            final JobWorker GatewayListenerWorker = client.newWorker()
                .jobType("isBudgetCodeOutdated")
                .handler(new GatewayListenerHandler())
                .open();
            
            final JobWorker UserTaskListenerWorker = client.newWorker()
                .jobType("assignTeamManager")
                .handler(new UserTaskListenerHandler())
                .open();
             
            // Terminate the worker with an Integer input
            Scanner sc = new Scanner(System.in);
            sc.nextInt();
            sc.close();
            SearchCustomerWorker.close();
            BudgetTrackerWorker.close();
            GatewayListenerWorker.close();
            UserTaskListenerWorker.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadProperties() {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream(CAMUNDA_PROPERTIES_PATH)) {
            properties.load(input);
            CAMUNDA_AUTHORIZATION_SERVER_URL = properties.getProperty("camunda.auth.server.url");
            CAMUNDA_CLIENT_ID = properties.getProperty("camunda.client.auth.client-id");
            CAMUNDA_CLIENT_SECRET = properties.getProperty("camunda.client.auth.client-secret");
            CAMUNDA_REST_ADDRESS = properties.getProperty("CAMUNDA_REST_ADDRESS");
            CAMUNDA_GRPC_ADDRESS = properties.getProperty("CAMUNDA_GRPC_ADDRESS");
            CAMUNDA_TOKEN_AUDIENCE = properties.getProperty("CAMUNDA_TOKEN_AUDIENCE");
        
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
