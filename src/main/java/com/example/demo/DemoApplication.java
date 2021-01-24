package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

@EnableFeignClients
@SpringBootApplication
@Import(FeignConfig.class)
public class DemoApplication {

    // minimal sample for https://github.com/spring-cloud/spring-cloud-sleuth/issues/1824
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
