package gg.wil.imposter.session.repo;

import com.google.gson.Gson;
import gg.wil.imposter.session.Player;
import gg.wil.imposter.session.SessionData;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
@ConditionalOnExpression("'${app.server.mode}'.toUpperCase() == 'GAME_SERVER'")
public class ServerSessionRepo extends LocalSessionRepo {

    private final ReactiveStringRedisTemplate redis;
    private final Gson gson = new Gson();

    public ServerSessionRepo(ReactiveStringRedisTemplate redis) {
        this.redis = redis;
    }

    @Override
    public Mono<Boolean> addSession(SessionData sessionData) {
        String playerJson = gson.toJson(sessionData);
        return this.redis.opsForValue().set("session:" + sessionData.sessionID(), playerJson);
    }

    @Override
    public void removeSession(Player player) {
        throw new UnsupportedOperationException("ServerSessionRepo does not support removeSession(Player)");
    }

    @Override
    public Mono<Long> removeSession(UUID sessionID) {
        super.removeSession(getSession(sessionID));
        return this.redis.delete("session:" + sessionID.toString());
    }

    @Override
    public Mono<SessionData> getSessionData(UUID sessionID) {
        return this.redis.opsForValue().get("session:" + sessionID)
                .map(session -> gson.fromJson(session, SessionData.class));
    }
}
