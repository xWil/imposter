package gg.wil.imposter.lobby.messages;

import java.util.UUID;

public record ServerCommand(String action, String lobbyCode, UUID hostSessionId, UUID hostPlayerId) {
}
