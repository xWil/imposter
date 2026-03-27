package gg.wil.imposter.repo;

import gg.wil.imposter.game.Lobby;

public interface LobbyRepo {

    boolean addLobby(Lobby lobby);
    Lobby getLobby(String lobbyCode);
    boolean removeLobby(String lobbyCode);
}
