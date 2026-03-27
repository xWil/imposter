package gg.wil.imposter.websocket;

import gg.wil.imposter.Config;
import gg.wil.imposter.exception.WebSocketException;
import gg.wil.imposter.session.Player;
import gg.wil.imposter.session.repo.SessionRepo;
import gg.wil.imposter.lobby.LobbyService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BandwidthBuilder;
import io.github.bucket4j.Bucket;
import org.jspecify.annotations.NullMarked;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.UUID;

@Component
@ConditionalOnExpression("'${app.server.mode}'.toUpperCase() == 'GAME_SERVER' || '${app.server.mode}'.toUpperCase() == 'BOTH'")
public class LobbyWebSocketHandler implements WebSocketHandler {

    private final LobbyService lobbyService;
    private final SessionRepo sessionRepo;

    public LobbyWebSocketHandler(LobbyService lobbyService, SessionRepo sessionRepo) {
        this.lobbyService = lobbyService;
        this.sessionRepo = sessionRepo;
    }

    @Override
    @NullMarked
    public Mono<Void> handle(WebSocketSession session) {

        final String lobbyCode = getLobbyCode(session);
        final UUID sessionID = getSessionID(session);

        // check credentials
        try {
            this.lobbyService.checkCredentials(lobbyCode, sessionID);
        } catch(WebSocketException e) {
            String message = "{\n  \"type:\": \"ERROR\",\n  \"code\": \"" + e.getType() + "\",\n \"message\": \"" + e.getMessage() + "\"\n}";
            return session.send(Mono.just(session.textMessage(message))).then(session.close());
        }

        final Player player = this.sessionRepo.getSession(sessionID);
        final UUID playerID = player.getUUID();

        Sinks.Many<String> outgoingSink = Sinks.many().unicast().onBackpressureBuffer();

        final Bandwidth bandwidth = BandwidthBuilder.builder().capacity(Config.WEBSOCKET_MESSAGES_PER_SECOND)
                .refillGreedy(Config.WEBSOCKET_MESSAGES_PER_SECOND, Duration.ofSeconds(1))
                .initialTokens(Config.WEBSOCKET_MESSAGES_PER_SECOND).build();
        final Bucket bucket = Bucket.builder().addLimit(bandwidth).build();

        this.lobbyService.playerConnected(lobbyCode, playerID, session, outgoingSink);
        Flux<WebSocketMessage> outgoing = outgoingSink.asFlux().map(session::textMessage);
        Mono<Void> incoming = session.receive()
                .filter(_ -> bucket.tryConsume(1))
                .map(WebSocketMessage::getPayloadAsText)
                .flatMap(msg -> this.lobbyService.handleMessage(lobbyCode, playerID, msg))
                .onErrorResume(_ -> Mono.empty())
                .then();

        return session.send(outgoing).and(incoming.doFinally(signalType -> {
            this.sessionRepo.removeConnection(player.getWebsocketIP());
            this.lobbyService.playerDisconnected(lobbyCode, playerID);
        }));
    }

    private String getLobbyCode(WebSocketSession session) {
        String path = session.getHandshakeInfo().getUri().getPath();
        return path.substring(path.lastIndexOf("/") + 1);
    }

    private UUID getSessionID(WebSocketSession session) {
        String id = UriComponentsBuilder.fromUri(session.getHandshakeInfo().getUri())
                .build().getQueryParams().getFirst("session");
        if(id == null) return null;

        try {
            return UUID.fromString(id);
        } catch(IllegalArgumentException e) {
            return null;
        }
    }
}
