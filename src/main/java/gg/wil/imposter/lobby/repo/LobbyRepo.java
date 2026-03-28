package gg.wil.imposter.lobby.repo;

import gg.wil.imposter.lobby.Lobby;
import gg.wil.imposter.lobby.LobbyData;
import reactor.core.publisher.Mono;

public interface LobbyRepo {

    boolean addLobby(Lobby lobby);
    Mono<Boolean> addLobbyData(LobbyData lobby);
    Lobby getLobby(String lobbyCode);
    Mono<LobbyData> getLobbyData(String lobbyCode);
    boolean removeLobby(String lobbyCode);
    Mono<Long> removeLobbyData(String lobbyCode);
}
