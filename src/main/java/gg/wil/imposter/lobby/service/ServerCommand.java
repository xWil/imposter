package gg.wil.imposter.lobby.service;

import java.util.UUID;

public record ServerCommand(String action, String lobbyCode, UUID sessionID, UUID playerID, String username) { }
