package com.honghuang.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {


    @Bean
    @LoadBalanced // 代表创建了一个具有负载均衡的restTemplate
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }
}
