package com.example.demo;

import feign.Client;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.cloud.openfeign.ribbon.CachingSpringLoadBalancerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public Client client(CachingSpringLoadBalancerFactory balancerFactory,
                                    SpringClientFactory clientFactory) {
        Client client = new SimpleClient();
        return new WrapperLoadBalancerFeignClient(client, balancerFactory, clientFactory);
    }

}
