package gg.wil.imposter.api.websocket;

import gg.wil.imposter.exception.WebSocketException;
import gg.wil.imposter.services.LobbyService;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.UUID;

@Component
public class LobbyWebSocketHandler implements WebSocketHandler {

    private final LobbyService lobbyService;

    public LobbyWebSocketHandler(LobbyService lobbyService) {
        this.lobbyService = lobbyService;
    }

    @Override
    @NullMarked
    public Mono<Void> handle(WebSocketSession session) {

        String lobbyCode = getLobbyCode(session);
        UUID playerID = getPlayerID(session);

        // check credentials
        try {
            this.lobbyService.checkCredentials(lobbyCode, playerID);
        } catch(WebSocketException e) {
            String message = "{\n  \"type:\": \"ERROR\",\n  \"code\": \"" + e.getType() + "\",\n \"message\": \"" + e.getMessage() + "\"\n}";
            return session.send(Mono.just(session.textMessage(message))).then(session.close());
        }

        Sinks.Many<String> outgoingSink = Sinks.many().unicast().onBackpressureBuffer();

        this.lobbyService.playerConnected(lobbyCode, playerID, session, outgoingSink);
        Flux<WebSocketMessage> outgoing = outgoingSink.asFlux().map(session::textMessage);
        Mono<Void> incoming = session.receive().map(WebSocketMessage::getPayloadAsText)
                .flatMap(msg -> this.lobbyService.handleMessage(lobbyCode, playerID, msg))
                .onErrorResume(_ -> Mono.empty())
                .then();

        return session.send(outgoing).and(incoming.doFinally(signalType -> this.lobbyService.playerDisconnected(lobbyCode, playerID)));
    }

    private String getLobbyCode(WebSocketSession session) {
        String path = session.getHandshakeInfo().getUri().getPath();
        return path.substring(path.lastIndexOf("/") + 1);
    }

    private UUID getPlayerID(WebSocketSession session) {
        String id = UriComponentsBuilder.fromUri(session.getHandshakeInfo().getUri())
                .build().getQueryParams().getFirst("playerId");
        return UUID.fromString(id);
    }
}
