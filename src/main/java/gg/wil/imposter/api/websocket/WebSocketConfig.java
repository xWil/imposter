package gg.wil.imposter.api.websocket;

import gg.wil.imposter.Config;
import gg.wil.imposter.exception.WebSocketException;
import gg.wil.imposter.game.Player;
import gg.wil.imposter.repo.SessionRepo;
import gg.wil.imposter.services.LobbyService;
import gg.wil.imposter.util.ImposterUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import org.springframework.web.reactive.socket.server.upgrade.ReactorNettyRequestUpgradeStrategy;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.WebsocketServerSpec;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Configuration
@EnableWebFlux
public class WebSocketConfig {

    private static Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

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
        ReactorNettyRequestUpgradeStrategy strategy = new ReactorNettyRequestUpgradeStrategy(() -> WebsocketServerSpec.builder().maxFramePayloadLength(Config.WEBSOCKET_MAX_SIZE));

        return new WebSocketHandlerAdapter(new HandshakeWebSocketService(strategy) {
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

                // check max connections
                String ip = ImposterUtil.getClientIP(exchange);
                SessionRepo repo = SessionRepo.getInstance();
                if(repo.getConnectionCount(ip) >= Config.WEBSOCKET_MAX_CONNECTIONS) {
                    logger.warn("Connection refused from {} due to having too many other connections.", ip);
                    exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                    return exchange.getResponse().setComplete();
                }
                repo.addConnection(ip);
                Player player = repo.getSession(sessionID);
                if(player != null) player.setWebsocketIP(ip);

                return super.handleRequest(exchange, handler).doOnError(_ -> repo.removeConnection(ip));
            }
        });
    }
}
