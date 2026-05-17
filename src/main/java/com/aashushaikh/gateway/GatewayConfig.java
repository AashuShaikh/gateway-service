package com.aashushaikh.gateway;

import org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.web.servlet.function.RequestPredicates.path;

@Configuration
public class GatewayConfig {

    @Bean
    public RouterFunction<ServerResponse> authRoute() {
        return GatewayRouterFunctions.route("auth-route")
                .route(path("/api/auth/**"), HandlerFunctions.http())
                .filter(LoadBalancerFilterFunctions.lb("auth"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> userRoute() {
        return GatewayRouterFunctions.route("user-route")
                .route(path("/api/users/**"), HandlerFunctions.http())
                .filter(LoadBalancerFilterFunctions.lb("user"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> messageRoute() {
        return GatewayRouterFunctions.route("message-route")
                .route(path("/api/messages/**"), HandlerFunctions.http())
                .filter(LoadBalancerFilterFunctions.lb("message"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> chatRoute() {
        return GatewayRouterFunctions.route("chat-route")
                .route(path("/api/chats/**"), HandlerFunctions.http())
                .filter(LoadBalancerFilterFunctions.lb("chat"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> notificationRoute() {
        return GatewayRouterFunctions.route("notification-route")
                .route(path("/api/notifications/**"), HandlerFunctions.http())
                .filter(LoadBalancerFilterFunctions.lb("notification"))
                .build();
    }
}
