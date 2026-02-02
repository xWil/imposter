package gg.wil.imposter.repo;

import gg.wil.imposter.game.Lobby;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentHashMap;

@Repository
public class LobbyRepo {

    private static LobbyRepo INSTANCE;
    public static void setInstance(LobbyRepo instance) {
        LobbyRepo.INSTANCE = instance;
    }
    public static LobbyRepo getInstance() {
        return INSTANCE;
    }

    private final ConcurrentHashMap<String, Lobby> lobbies = new ConcurrentHashMap<>();

    public final boolean addLobby(Lobby lobby) {
        if(lobby == null) return false;
        if(lobbies.containsKey(lobby.getLobbyCode())) return false;

        lobbies.put(lobby.getLobbyCode(), lobby);
        return true;
    }

    public final Lobby getLobby(String lobbyCode) {
        return lobbies.get(lobbyCode.toUpperCase());
    }

    public final boolean removeLobby(String lobbyCode) {
        return lobbies.remove(lobbyCode) != null;
    }
}
