package gg.wil.imposter.session.repo;

import gg.wil.imposter.lobby.Lobby;
import gg.wil.imposter.session.Player;
import gg.wil.imposter.session.SessionData;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@ConditionalOnExpression("'${app.server.mode}'.toUpperCase() == 'BOTH'")
public class LocalSessionRepo implements SessionRepo {

    // IP, connection count
    private final ConcurrentHashMap<String, Integer> connections = new ConcurrentHashMap<>();

    @Override
    public void addConnection(String ip) {
        this.connections.merge(ip, 1, Integer::sum);
    }

    @Override
    public void removeConnection(String ip) {
        if(ip == null) return;
        this.connections.computeIfPresent(ip, (_, count) -> count <= 1 ? null : count - 1);
    }

    @Override
    public int getConnectionCount(String ip) {
        return connections.getOrDefault(ip, 0);
    }

    // SessionID, Player
    private final ConcurrentHashMap<UUID, Player> sessions = new ConcurrentHashMap<>();
    // PlayerID, Lobby
    private final ConcurrentHashMap<UUID, Lobby> lobbies = new ConcurrentHashMap<>();

    @Override
    public void addSession(Player player, Lobby lobby) {
        sessions.put(player.getSessionID(), player);
        lobbies.put(player.getUUID(), lobby);
    }

    @Override
    public void addSession(SessionData sessionData) {
        throw new UnsupportedOperationException("LocalSessionRepo does not support addSession(PlayerData)");
    }

    @Override
    public void removeSession(Player player) {
        sessions.remove(player.getSessionID());
        lobbies.remove(player.getUUID());
    }

    @Override
    public void removeSession(SessionData sessionData) {
        throw new UnsupportedOperationException("LocalSessionRepo does not support removeSession(PlayerData)");
    }

    @Override
    public Player getSession(UUID sessionID) {
        return sessions.get(sessionID);
    }

    @Override
    public SessionData getSessionData(UUID sessionID) {
        throw new UnsupportedOperationException("LocalSessionRepo does not support getSessionData");
    }

    @Override
    public Lobby getLobby(UUID playerID) {
        return lobbies.get(playerID);
    }
}
