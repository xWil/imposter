package gg.wil.imposter.repo.lobby;

import com.google.gson.Gson;
import gg.wil.imposter.game.Lobby;
import gg.wil.imposter.repo.LobbyRepo;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnExpression("'${app.server.mode}'.toUpperCase() == 'PROXY'")
public class ProxyLobbyRepo implements LobbyRepo {

    private final ReactiveStringRedisTemplate redis;
    private final Gson gson = new Gson();

    public ProxyLobbyRepo(ReactiveStringRedisTemplate redis) {
        this.redis = redis;
    }

    @Override
    public boolean addLobby(Lobby lobby) {
        throw new UnsupportedOperationException("RedisLobbyRepo does not support addLobby");
    }

    @Override
    public boolean addLobbyData(LobbyData lobbyData) {
        String json = gson.toJson(lobbyData);
        return Boolean.TRUE.equals(this.redis.opsForValue().setIfAbsent("lobby:" + lobbyData.lobbyCode().toUpperCase(), json).block());
    }

    @Override
    public Lobby getLobby(String lobbyCode) {
        throw new UnsupportedOperationException("RedisLobbyRepo does not support getLobby");
    }

    @Override
    public LobbyData getLobbyData(String lobbyCode) {
        String json = this.redis.opsForValue().get("lobby:" + lobbyCode.toUpperCase()).block();
        if(json == null) return null;
        return gson.fromJson(json, LobbyData.class);
    }

    @Override
    public boolean removeLobby(String lobbyCode) {
        Long deleted = this.redis.delete("lobby:" + lobbyCode.toUpperCase()).block();
        return deleted != null && deleted > 0;
    }
}
