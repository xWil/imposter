package gg.wil.imposter.exception.lobby;

import gg.wil.imposter.exception.LobbyException;

public class PlayerNotAllowedException extends LobbyException {

    public PlayerNotAllowedException(String lobbyCode) {
        super("Lobby " + lobbyCode + " does not have that player.", LobbyExceptionType.PLAYER_NOT_ALLOWED, lobbyCode, 409);
    }
}
