package gg.wil.imposter.repo;

import gg.wil.imposter.game.Lobby;
import gg.wil.imposter.repo.lobby.LobbyData;

public interface LobbyRepo {

    boolean addLobby(Lobby lobby);
    boolean addLobbyData(LobbyData lobby);
    Lobby getLobby(String lobbyCode);
    LobbyData getLobbyData(String lobbyCode);
    boolean removeLobby(String lobbyCode);
}
