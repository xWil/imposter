package gg.wil.imposter.repo.session;

import gg.wil.imposter.game.Lobby;
import gg.wil.imposter.game.Player;
import gg.wil.imposter.repo.SessionRepo;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class LocalSessionRepo implements SessionRepo {

    // IP, connection count
    private final ConcurrentHashMap<String, Integer> connections = new ConcurrentHashMap<>();

    public void addConnection(String ip) {
        this.connections.merge(ip, 1, Integer::sum);
    }

    public void removeConnection(String ip) {
        if(ip == null) return;
        this.connections.computeIfPresent(ip, (_, count) -> count <= 1 ? null : count - 1);
    }

    public int getConnectionCount(String ip) {
        return connections.getOrDefault(ip, 0);
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
