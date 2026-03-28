package gg.wil.imposter.session.repo;

import com.google.gson.Gson;
import gg.wil.imposter.lobby.Lobby;
import gg.wil.imposter.session.Player;
import gg.wil.imposter.session.SessionData;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
@ConditionalOnExpression("'${app.server.mode}'.toUpperCase() == 'PROXY'")
public class ProxySessionRepo implements SessionRepo {

    private final ReactiveStringRedisTemplate redis;
    private final Gson gson = new Gson();

    public ProxySessionRepo(ReactiveStringRedisTemplate redis) {
        this.redis = redis;
    }

    @Override
    public void addConnection(String ip) {
        this.redis.opsForValue().increment("connection:" + ip).block();
    }

    @Override
    public void removeConnection(String ip) {
        Long count = this.redis.opsForValue().decrement("connection:" + ip).block();
        // Clean up the key if the count reaches 0 to save memory
        if (count != null && count <= 0) {
            this.redis.delete("connection:" + ip).block();
        }
    }

    @Override
    public int getConnectionCount(String ip) {
        String count = this.redis.opsForValue().get("connection:" + ip).block();
        return count == null ? 0 : Integer.parseInt(count);
    }

    @Override
    public void addSession(Player player, Lobby lobby) {
        throw new UnsupportedOperationException("ProxySessionRepo does not support addSession(Player, Lobby)");
    }

    @Override
    public Mono<Boolean> addSession(SessionData sessionData) {
        String playerJson = gson.toJson(sessionData);
        return this.redis.opsForValue().set("session:" + sessionData.sessionID(), playerJson);
    }

    @Override
    public void removeSession(Player player) {
        throw new UnsupportedOperationException("ProxySessionRepo does not support removeSession(Player)");
    }

    @Override
    public Mono<Long> removeSession(UUID sessionID) {
        return this.redis.delete("session:" + sessionID.toString());
    }

    @Override
    public Player getSession(UUID sessionID) {
        throw new UnsupportedOperationException("ProxySessionRepo does not support getSession");
    }

    @Override
    public Mono<SessionData> getSessionData(UUID sessionID) {
        return this.redis.opsForValue().get("session:" + sessionID)
                .map(session -> gson.fromJson(session, SessionData.class));
    }

    @Override
    public Lobby getLobby(UUID playerID) {
        throw new UnsupportedOperationException("ProxySessionRepo does not support getLobby");
    }
}
