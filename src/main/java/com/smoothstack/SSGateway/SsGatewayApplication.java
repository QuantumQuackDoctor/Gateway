package com.smoothstack.SSGateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SsGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(SsGatewayApplication.class, args);
    }

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/accounts/**", "/accounts")
                        .uri("http://user")) //user service link
                .route(r -> r.path("/order/**", "/order" )
                        .uri("http://order")) //order service link
                .route(r -> r.path("/restaurant/**", "/restaurant" )
                        .uri("http://restaurant")) //restaurant service link
                .build();
    }
}
