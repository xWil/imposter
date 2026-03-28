package gg.wil.imposter.lobby.repo;

import com.google.gson.Gson;
import gg.wil.imposter.lobby.Lobby;
import gg.wil.imposter.lobby.LobbyData;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

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
        throw new UnsupportedOperationException("ProxyLobbyRepo does not support addLobby");
    }

    @Override
    public Mono<Boolean> addLobbyData(LobbyData lobbyData) {
        String json = gson.toJson(lobbyData);
        return this.redis.opsForValue().setIfAbsent("lobby:" + lobbyData.lobbyCode().toUpperCase(), json);
    }

    @Override
    public Lobby getLobby(String lobbyCode) {
        throw new UnsupportedOperationException("ProxyLobbyRepo does not support getLobby");
    }

    @Override
    public Mono<LobbyData> getLobbyData(String lobbyCode) {
        return this.redis.opsForValue().get("lobby:" + lobbyCode.toUpperCase())
                .map(json -> gson.fromJson(json, LobbyData.class));
    }

    @Override
    public boolean removeLobby(String lobbyCode) {
        throw new UnsupportedOperationException("ProxyLobbyRepo does not support removeLobby");
    }

    @Override
    public Mono<Long> removeLobbyData(String lobbyCode) {
        return this.redis.delete("lobby:" + lobbyCode.toUpperCase());
    }
}
