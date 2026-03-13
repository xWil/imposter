package gg.wil.imposter.repo;

import gg.wil.imposter.game.Lobby;
import gg.wil.imposter.game.Player;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class SessionRepo {

    private static SessionRepo INSTANCE;
    public static void setInstance(SessionRepo instance) {
        SessionRepo.INSTANCE = instance;
    }
    public static SessionRepo getInstance() {
        return INSTANCE;
    }

    // SessionID, Player
    private final ConcurrentHashMap<UUID, Player> sessions = new ConcurrentHashMap<>();
    // PlayerID, Lobby
    private final ConcurrentHashMap<UUID, Lobby> lobbies = new ConcurrentHashMap<>();

    public void addSession(Player player, Lobby lobby) {
        sessions.put(player.getSessionID(), player);
        lobbies.put(player.getUUID(), lobby);
    }

    public void removeSession(Player player) {
        sessions.remove(player.getSessionID());
        lobbies.remove(player.getUUID());
    }

    public Player getSession(UUID sessionID) {
        return sessions.get(sessionID);
    }

    public Lobby getLobby(UUID playerID) {
        return lobbies.get(playerID);
    }
}
