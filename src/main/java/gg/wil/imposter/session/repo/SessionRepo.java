package gg.wil.imposter.session.repo;

import gg.wil.imposter.lobby.Lobby;
import gg.wil.imposter.session.Player;
import gg.wil.imposter.session.SessionData;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface SessionRepo {

    void addConnection(String ip);
    void removeConnection(String ip);
    int getConnectionCount(String ip);

    void addSession(Player player, Lobby lobby);
    Mono<Boolean> addSession(SessionData sessionData);
    void removeSession(Player player);
    Mono<Long> removeSession(UUID sessionID);
    Player getSession(UUID sessionID);
    Mono<SessionData> getSessionData(UUID sessionID);
    Lobby getLobby(UUID playerID);
}
