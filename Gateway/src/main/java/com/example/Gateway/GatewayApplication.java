package com.example.Gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;

@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p
                        .path("/profiles").and().method(HttpMethod.GET)
                        .filters(f -> f.prefixPath("/PS"))
                        .uri("http://localhost:8080"))
                .route(p -> p
                        .path("/profile/{id}/{endpoint}")
                        .filters(f -> f.prefixPath("/PS"))
                        .uri("http://localhost:8080"))
                .route(p -> p
                        .path("/profile/{id}/token")
                        .filters(f -> f.rewritePath("/profile", "/AS/user"))
                        .uri("http://localhost:8081"))
                .build();
    }
}