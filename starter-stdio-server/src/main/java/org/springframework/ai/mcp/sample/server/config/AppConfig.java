package org.springframework.ai.mcp.sample.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @Author: hzh
 * @CreateTime:2025-08-07
 */
@Configuration
public class AppConfig {

    @Bean
    public RestTemplate  restTemplate(){
        return new RestTemplate();
    }
}
