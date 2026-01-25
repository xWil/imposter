package gg.wil.imposter.exception.lobby;

import gg.wil.imposter.exception.LobbyException;

public class AlreadyInLobbyException extends LobbyException {

    public AlreadyInLobbyException(String lobbyCode) {
        super("Player is already in lobby " + lobbyCode, LobbyExceptionType.ALREADY_IN_LOBBY, lobbyCode, 409);
    }
}
