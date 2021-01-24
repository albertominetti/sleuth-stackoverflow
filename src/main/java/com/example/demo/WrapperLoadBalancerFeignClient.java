package com.example.demo;

import feign.Client;
import feign.Request;
import feign.Response;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.cloud.openfeign.ribbon.CachingSpringLoadBalancerFactory;
import org.springframework.cloud.openfeign.ribbon.LoadBalancerFeignClient;

import java.io.IOException;

public class WrapperLoadBalancerFeignClient extends LoadBalancerFeignClient {
    public WrapperLoadBalancerFeignClient(Client client, CachingSpringLoadBalancerFactory balancerFactory, SpringClientFactory clientFactory) {
        super(client, balancerFactory, clientFactory);
    }

    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        try {
            return super.execute(request, options);
        } catch (IOException | RuntimeException e) {
            // I would like to throw a specific exception instead of FeignServerException
            throw new ServerUnavailableException("xxxx");
        }
    }
}
