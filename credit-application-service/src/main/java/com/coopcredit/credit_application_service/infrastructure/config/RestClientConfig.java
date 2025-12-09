package com.coopcredit.credit_application_service.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Configuration for RestClient to communicate with external Risk Central service
 */
@Configuration
public class RestClientConfig {

    public static final String RISK_CENTRAL_BASE_URL = "http://localhost:8081";

    @Bean
    public RestClient riskCentralRestClient() {
        return RestClient.builder()
                .baseUrl(RISK_CENTRAL_BASE_URL)
                .build();
    }
}
