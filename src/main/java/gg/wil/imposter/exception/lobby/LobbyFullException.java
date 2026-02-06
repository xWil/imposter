package gg.wil.imposter.exception.lobby;

import gg.wil.imposter.exception.LobbyException;

public class LobbyFullException extends LobbyException {

    public LobbyFullException(String lobbyCode) {
        super("Lobby " + lobbyCode + " is full", LobbyExceptionType.LOBBY_FULL, lobbyCode, 409);
    }
}
