package gg.wil.imposter.session.repo;

import com.google.gson.Gson;
import gg.wil.imposter.session.Player;
import gg.wil.imposter.session.SessionData;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Repository;

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
    public void addSession(SessionData sessionData) {
        String playerJson = gson.toJson(sessionData);
        this.redis.opsForValue().set("session:" + sessionData.sessionID(), playerJson).block();
    }

    @Override
    public void removeSession(Player player) {
        super.removeSession(player);
        this.redis.delete("session:" + player.getSessionID()).block();
    }

    @Override
    public void removeSession(SessionData sessionData) {
        this.redis.delete("session:" + sessionData.sessionID()).block();
    }

    @Override
    public SessionData getSessionData(UUID sessionID) {
        String json = this.redis.opsForValue().get("session:" + sessionID).block();
        if(json == null) return null;
        return gson.fromJson(json, SessionData.class);
    }
}
