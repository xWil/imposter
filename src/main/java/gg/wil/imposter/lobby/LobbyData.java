package gg.wil.imposter.lobby;

public record LobbyData(String lobbyCode, String gameServerURL, String hostID, int playerCount, String state) { }
