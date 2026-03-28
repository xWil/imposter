package gg.wil.imposter.lobby.repo;

import com.google.gson.Gson;
import gg.wil.imposter.lobby.Lobby;
import gg.wil.imposter.lobby.LobbyData;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@ConditionalOnExpression("'${app.server.mode}'.toUpperCase() == 'GAME_SERVER'")
public class ServerLobbyRepo extends LocalLobbyRepo {

    private final ReactiveStringRedisTemplate redis;
    private final Gson gson = new Gson();

    public ServerLobbyRepo(ReactiveStringRedisTemplate redis) {
        this.redis = redis;
    }

    @Override
    public Mono<Boolean> addLobbyData(LobbyData lobbyData) {
        String json = gson.toJson(lobbyData);
        return this.redis.opsForValue().setIfAbsent("lobby:" + lobbyData.lobbyCode().toUpperCase(), json);
    }

    @Override
    public Mono<LobbyData> getLobbyData(String lobbyCode) {
        return this.redis.opsForValue().get("lobby:" + lobbyCode.toUpperCase())
                .map(json -> gson.fromJson(json, LobbyData.class));
    }

    @Override
    public boolean removeLobby(String lobbyCode) {
        throw new UnsupportedOperationException("ServerLobbyRepo does not support removeLobby");
    }

    @Override
    public Mono<Long> removeLobbyData(String lobbyCode) {
        super.removeLobby(lobbyCode);
        return this.redis.delete("lobby:" + lobbyCode.toUpperCase());
    }

    public void updateLobbyData(Lobby lobby) {
        final String key = "lobby:" + lobby.getLobbyCode().toUpperCase();
        this.redis.opsForValue().get(key).subscribe(json -> {
            LobbyData current = gson.fromJson(json, LobbyData.class);
            LobbyData updated = new LobbyData(current.lobbyCode(), current.serverID(), current.gameServerURL(), current.hostID(), lobby.getPlayers().size(), lobby.getState().toString());
            this.redis.opsForValue().set(key, gson.toJson(updated)).subscribe();
        });
    }
}
