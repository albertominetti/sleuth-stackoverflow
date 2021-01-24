package com.example.demo;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("sample-client")
public interface SampleClient {
    @GetMapping("instruments/{id}")
    String findInstrumentById(@PathVariable("id") String id);
}
