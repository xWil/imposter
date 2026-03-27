package gg.wil.imposter.lobby.repo;

import gg.wil.imposter.lobby.Lobby;
import gg.wil.imposter.lobby.LobbyData;

public interface LobbyRepo {

    boolean addLobby(Lobby lobby);
    boolean addLobbyData(LobbyData lobby);
    Lobby getLobby(String lobbyCode);
    LobbyData getLobbyData(String lobbyCode);
    boolean removeLobby(String lobbyCode);
}
