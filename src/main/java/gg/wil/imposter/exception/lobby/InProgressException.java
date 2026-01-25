package gg.wil.imposter.exception.lobby;

import gg.wil.imposter.exception.LobbyException;

public class InProgressException extends LobbyException {

    public InProgressException(String lobbyCode) {
        super("Lobby " + lobbyCode + " is currently playing", LobbyExceptionType.GAME_IN_PROGRESS, lobbyCode, 409);
    }
}
