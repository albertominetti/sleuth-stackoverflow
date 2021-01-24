package com.example.demo;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(HttpMessageConverter.class)
@AutoConfigureAfter({JacksonAutoConfiguration.class})
public class CustomHttpMessageConvertersAUtoConfiguration extends HttpMessageConvertersAutoConfiguration {
}
