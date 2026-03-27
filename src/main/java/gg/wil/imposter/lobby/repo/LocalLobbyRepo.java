package gg.wil.imposter.lobby.repo;

import gg.wil.imposter.lobby.Lobby;
import gg.wil.imposter.lobby.LobbyData;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentHashMap;

@Repository
@ConditionalOnExpression("'${app.server.mode}'.toUpperCase() == 'BOTH'")
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
    public boolean addLobbyData(LobbyData lobbyData) {
        throw new UnsupportedOperationException("LocalLobbyRepo does not support addLobbyData");
    }

    @Override
    public Lobby getLobby(String lobbyCode) {
        return lobbies.get(lobbyCode.toUpperCase());
    }

    @Override
    public LobbyData getLobbyData(String lobbyCode) {
        throw new UnsupportedOperationException("LocalLobbyRepo does not support getLobbyData");
    }

    @Override
    public boolean removeLobby(String lobbyCode) {
        return lobbies.remove(lobbyCode) != null;
    }
}
