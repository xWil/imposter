package gg.wil.imposter.lobby.service;

import gg.wil.imposter.api.messages.LobbyResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.UUID;

@Service
public interface LobbyService {

    Mono<LobbyResponse> createLobby();
    Mono<LobbyResponse> joinLobby(String lobbyCode, String username);
    Mono<LobbyResponse> rejoinLobby(UUID sessionID);
    void playerConnected(String lobbyCode, UUID playerID, WebSocketSession session, Sinks.Many<String> outgoingSink);
    Mono<Void> handleMessage(String lobbyCode, UUID playerID, String message);
    void playerDisconnected(String lobbyCode, UUID playerID);
    void checkCredentials(String lobbyCode, UUID sessionID);

    default boolean checkUsername(String username) {
        if(username == null) return false;

        username = username.trim();
        if(username.length() > 16 || username.isEmpty()) return false;
        if(username.chars().anyMatch(Character::isISOControl)) return false;
        if(username.chars().anyMatch(c ->
                c == 0x200B || // zero-width space
                c == 0x200C || // zero-width non-joiner
                c == 0x200D || // zero-width joiner
                c == 0xFEFF    // zero-width no-break space
        )) return false;

        // a
        return username.matches("^[a-zA-Z0-9_ .!$-]+$");
    }
}
