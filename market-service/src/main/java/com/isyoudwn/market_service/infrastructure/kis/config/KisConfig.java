package com.isyoudwn.market_service.infrastructure.kis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class KisConfig {

    @Bean
    public RestClient kisRestClient(
            KisProperties kisProperties
    ) {
        return RestClient.builder()
                .baseUrl(kisProperties.restBaseUrl())
                .build();
    }
}
