package ru.flamexander.reactive.service.configurations;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.util.concurrent.TimeUnit;

@Configuration
public class IntegrationsConfig {
    @Bean
    public WebClient productDetailsServiceWebClient() {
        WebClient.Builder builder = WebClient.builder();
        HttpClient httpClient = HttpClient.create();
        httpClient
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1)
                .doOnConnected(it -> it
                        .addHandlerLast(new WriteTimeoutHandler(2500, TimeUnit.MILLISECONDS))
                        .addHandlerLast(new ReadTimeoutHandler(30000, TimeUnit.MILLISECONDS))
                );
        builder.clientConnector(new ReactorClientHttpConnector(httpClient));
        return builder
                .baseUrl("http://localhost:8190")
                .build();
    }
}
