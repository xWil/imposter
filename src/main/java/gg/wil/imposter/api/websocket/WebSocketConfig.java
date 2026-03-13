package gg.wil.imposter.api.websocket;

import gg.wil.imposter.exception.WebSocketException;
import gg.wil.imposter.services.LobbyService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Configuration
@EnableWebFlux
public class WebSocketConfig {

    @Bean
    public HandlerMapping webSocketMapping(LobbyWebSocketHandler handler) {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/ws/lobby/**", handler);

        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setUrlMap(map);
        mapping.setOrder(10);

        return mapping;
    }

    @Bean
    public WebSocketHandlerAdapter handlerAdapter(LobbyService lobbyService) {
        return new WebSocketHandlerAdapter(new HandshakeWebSocketService() {
            @Override
            public Mono<Void> handleRequest(ServerWebExchange exchange, WebSocketHandler handler) {
                String path = exchange.getRequest().getURI().getPath();
                String lobbyCode = path.substring(path.lastIndexOf("/") + 1);

                String id = exchange.getRequest().getQueryParams().getFirst("session");
                UUID sessionID = null;
                if (id != null) {
                    try {
                        sessionID = UUID.fromString(id);
                    } catch (IllegalArgumentException ignored) {}
                }

                try {
                    lobbyService.checkCredentials(lobbyCode, sessionID);
                } catch (WebSocketException e) {
                    exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
                    return exchange.getResponse().setComplete();
                }

                return super.handleRequest(exchange, handler);
            }
        });
    }
}
