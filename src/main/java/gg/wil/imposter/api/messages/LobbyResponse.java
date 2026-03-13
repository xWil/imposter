package gg.wil.imposter.api.messages;

public record LobbyResponse(String lobbyCode, String sessionID, String playerID, String websocketUrl) { }
