package com.smoothstack.SSGateway;

import com.smoothstack.SSGateway.filter.AuthenticationFilter;
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
	public RouteLocator gatewayRoutes(RouteLocatorBuilder builder, AuthenticationFilter filter){
		return builder.routes()
				.route(r -> r.path("/user", "/users", "/login", "/register")
						.filters(f -> f.filter(filter))
						.uri("http://localhost:8080"))
				.build();
	}
}
