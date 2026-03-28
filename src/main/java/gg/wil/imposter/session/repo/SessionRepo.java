package gg.wil.imposter.session.repo;

import gg.wil.imposter.lobby.Lobby;
import gg.wil.imposter.session.Player;
import gg.wil.imposter.session.SessionData;

import java.util.UUID;

public interface SessionRepo {

    void addConnection(String ip);
    void removeConnection(String ip);
    int getConnectionCount(String ip);

    void addSession(Player player, Lobby lobby);
    void addSession(SessionData sessionData);
    void removeSession(Player player);
    void removeSession(SessionData sessionData);
    Player getSession(UUID sessionID);
    SessionData getSessionData(UUID sessionID);
    Lobby getLobby(UUID playerID);
}
