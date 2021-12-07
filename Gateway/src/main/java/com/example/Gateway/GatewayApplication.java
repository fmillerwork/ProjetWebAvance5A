package com.example.Gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;

@SpringBootApplication
public class GatewayApplication {

    @Value("${service.authentication}")
    private String auth_service_url;
    @Value("${service.profiles}")
    private String profile_service_url;

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p
                        .path("/profiles").and().method(HttpMethod.GET)
                        .filters(f -> f.prefixPath("/PS"))
                        .uri(profile_service_url))
                .route(p -> p
                        .path("/profiles/{id}/token")
                        .filters(f -> f.rewritePath("/profiles", "/AS/users"))
                        .uri(auth_service_url))
                .route(p -> p
                        .path("/profiles/{id}/password")
                        .filters(f -> f.rewritePath("/profiles", "/AS/users"))
                        .uri(auth_service_url))
                .route(p -> p
                        .path("/profiles/{id}/{endpoint}")
                        .filters(f -> f.prefixPath("/PS"))
                        .uri(profile_service_url))
                .route(p -> p
                        .path("/login")
                        .filters(f -> f.prefixPath("/PS"))
                        .uri(profile_service_url))

                .build();
    }
}
