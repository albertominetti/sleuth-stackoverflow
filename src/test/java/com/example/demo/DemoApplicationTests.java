package com.example.demo;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.http.Fault;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerList;
import feign.Client;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.netflix.ribbon.StaticServerList;
import org.springframework.cloud.openfeign.ribbon.LoadBalancerFeignClient;
import org.springframework.context.annotation.Bean;

import java.lang.reflect.Field;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DemoApplicationTests {

    private static final Logger log = LoggerFactory.getLogger(DemoApplicationTests.class);

    static WireMockServer wireMockServer = new WireMockServer(new WireMockConfiguration().dynamicPort());

    @Autowired
    Client client;

    @Autowired
    SampleClient sampleClient;

    @BeforeAll
    static void beforeAll() {
        wireMockServer.start();
    }

    @AfterAll
    static void afterAll() {
        wireMockServer.stop();
    }

    @Test
    void testforMultipleDecorators() throws IllegalAccessException, NoSuchFieldException {
        wireMockServer.stubFor(get(urlEqualTo("/instruments/1234"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{'status':'ok'}".replace("'", "\""))
                )
        );

        sampleClient.findInstrumentById("1234");
        sampleClient.findInstrumentById("1234");
        sampleClient.findInstrumentById("1234");
        sampleClient.findInstrumentById("1234");
        sampleClient.findInstrumentById("1234");

        assertThat(client, is(notNullValue()));
        assertThat(client, is(instanceOf(LoadBalancerFeignClient.class)));

        LoadBalancerFeignClient balancer = (LoadBalancerFeignClient) this.client;

        Client delegate = balancer.getDelegate();
        int count = 0; // total decorators and final client
        while (hasField(delegate, "delegate")) {
            Field delegateField = delegate.getClass().getDeclaredField("delegate");
            delegateField.setAccessible(true);
            delegate = (Client) delegateField.get(delegate);
            count++;
            log.warn("Found {}", delegate);
        }

        assertThat(delegate, is(instanceOf(SimpleClient.class)));
        assertThat(count, is(lessThanOrEqualTo(2))); // expected LazyTracingFeignClient and SimpleClient
    }

    private static boolean hasField(Client delegate, String fieldName) {
        try {
            delegate.getClass().getDeclaredField(fieldName);
            return true;
        } catch (NoSuchFieldException e) {
            return false;
        }
    }


    @Test
    void testforMyExpectedException() throws NoSuchFieldException {
        wireMockServer.stubFor(get(urlEqualTo("/instruments/1234"))
                .willReturn(aResponse()
                        .withFault(Fault.CONNECTION_RESET_BY_PEER)
                )
        );

        Executable executable = () -> sampleClient.findInstrumentById("1234");

        assertThrows(ServerUnavailableException.class, executable);
    }


    @Test
    @Disabled
        /*
         * this throws the StackOverflowError
         * java.lang.StackOverflowError
         * at java.base/java.lang.StringBuilder.append(StringBuilder.java:173)
         * at java.base/sun.net.www.ParseUtil.appendAuthority(ParseUtil.java:416)
         * at java.base/sun.net.www.ParseUtil.appendSchemeSpecificPart(ParseUtil.java:383)
         * at java.base/sun.net.www.ParseUtil.toString(ParseUtil.java:345)
         * at java.base/sun.net.www.ParseUtil.createURI(ParseUtil.java:323)
         * at java.base/sun.net.www.ParseUtil.toURI(ParseUtil.java:300)
         * at java.base/sun.net.www.protocol.http.HttpURLConnection.plainConnect0(HttpURLConnection.java:1178)
         * at java.base/sun.net.www.protocol.http.HttpURLConnection.plainConnect(HttpURLConnection.java:1082)
         * at java.base/sun.net.www.protocol.http.HttpURLConnection.connect(HttpURLConnection.java:1016)
         * at java.base/sun.net.www.protocol.http.HttpURLConnection.getInputStream0(HttpURLConnection.java:1600)
         * at java.base/sun.net.www.protocol.http.HttpURLConnection.getInputStream(HttpURLConnection.java:1528)
         * at java.base/java.net.HttpURLConnection.getResponseCode(HttpURLConnection.java:527)
         * at feign.Client$Default.convertResponse(Client.java:108)
         * at feign.Client$Default.execute(Client.java:104)
         * at org.springframework.cloud.sleuth.instrument.web.client.feign.TracingFeignClient.execute(TracingFeignClient.java:81)
         * at org.springframework.cloud.sleuth.instrument.web.client.feign.LazyTracingFeignClient.execute(LazyTracingFeignClient.java:60)
         * at org.springframework.cloud.sleuth.instrument.web.client.feign.TracingFeignClient.execute(TracingFeignClient.java:81)
         * at org.springframework.cloud.sleuth.instrument.web.client.feign.LazyTracingFeignClient.execute(LazyTracingFeignClient.java:60)
         * at org.springframework.cloud.sleuth.instrument.web.client.feign.TracingFeignClient.execute(TracingFeignClient.java:81)
         * at org.springframework.cloud.sleuth.instrument.web.client.feign.LazyTracingFeignClient.execute(LazyTracingFeignClient.java:60)
         * at org.springframework.cloud.sleuth.instrument.web.client.feign.TracingFeignClient.execute(TracingFeignClient.java:81)
         * at org.springframework.cloud.sleuth.instrument.web.client.feign.LazyTracingFeignClient.execute(LazyTracingFeignClient.java:60)
         * at org.springframework.cloud.sleuth.instrument.web.client.feign.TracingFeignClient.execute(TracingFeignClient.java:81)
         * at org.springframework.cloud.sleuth.instrument.web.client.feign.LazyTracingFeignClient.execute(LazyTracingFeignClient.java:60)
         * ...
         */
    void stackOverflow() throws IllegalAccessException, NoSuchFieldException {
        wireMockServer.stubFor(get(urlEqualTo("/instruments/1234"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{'status':'ok'}".replace("'", "\""))
                )
        );

        for (int i = 0; i < 10000; i++) {
            sampleClient.findInstrumentById("1234");
        }

    }


    @TestConfiguration
    static class ContextConfig {
        @Bean
        public ServerList<Server> ribbonServerList() {
            return new StaticServerList<>(new Server("localhost", wireMockServer.port()));
        }
    }
}
