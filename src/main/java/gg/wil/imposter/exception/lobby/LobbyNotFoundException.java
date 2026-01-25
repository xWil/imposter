package gg.wil.imposter.exception.lobby;

import gg.wil.imposter.exception.LobbyException;

public class LobbyNotFoundException extends LobbyException {

    public LobbyNotFoundException(String lobbyCode) {
        super("Lobby " + lobbyCode + " was not found", LobbyExceptionType.LOBBY_NOT_FOUND, lobbyCode, 404);
    }
}
