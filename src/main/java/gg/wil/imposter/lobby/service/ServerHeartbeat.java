package gg.wil.imposter.lobby.service;

public record ServerHeartbeat(String serverID, String serverURL, int activeLobbies) { }
