package gg.wil.imposter.repo.lobby;

import gg.wil.imposter.game.Lobby;
import gg.wil.imposter.repo.LobbyRepo;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentHashMap;

@Repository
public class LocalLobbyRepo implements LobbyRepo {

    private final ConcurrentHashMap<String, Lobby> lobbies = new ConcurrentHashMap<>();

    @Override
    public boolean addLobby(Lobby lobby) {
        if(lobby == null) return false;
        if(lobbies.containsKey(lobby.getLobbyCode().toUpperCase())) return false;

        lobbies.put(lobby.getLobbyCode(), lobby);
        return true;
    }

    @Override
    public Lobby getLobby(String lobbyCode) {
        return lobbies.get(lobbyCode.toUpperCase());
    }

    @Override
    public boolean removeLobby(String lobbyCode) {
        return lobbies.remove(lobbyCode) != null;
    }
}
