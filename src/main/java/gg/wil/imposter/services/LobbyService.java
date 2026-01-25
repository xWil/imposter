package gg.wil.imposter.services;

import gg.wil.imposter.api.model.LobbyResponse;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LobbyService {

    public final LobbyResponse createLobby(String username) {
        return new LobbyResponse("test", "ws://localhost:8080/ws", UUID.randomUUID().toString());
    }

    public final LobbyResponse joinLobby(String lobbyCode, String username) {
        return new LobbyResponse(lobbyCode, "ws://localhost:8080/ws", UUID.randomUUID().toString());
    }
}
