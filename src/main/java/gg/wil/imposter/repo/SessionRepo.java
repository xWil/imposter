package gg.wil.imposter.repo;

import gg.wil.imposter.game.Lobby;
import gg.wil.imposter.game.Player;

import java.util.UUID;

public interface SessionRepo {

    void addConnection(String ip);
    void removeConnection(String ip);
    int getConnectionCount(String ip);

    void addSession(Player player, Lobby lobby);
    void removeSession(Player player);
    Player getSession(UUID sessionID);
    Lobby getLobby(UUID playerID);
}
