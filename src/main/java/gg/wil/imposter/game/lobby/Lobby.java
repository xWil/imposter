package gg.wil.imposter.game.lobby;

import gg.wil.imposter.game.Player;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Lobby {

    private final String lobbyCode;
    private LobbyState state;
    private final Player host;
    private final ConcurrentHashMap<UUID, Player> players = new ConcurrentHashMap<>();

    private Lobby(String lobbyCode, Player host) {
        this.lobbyCode = lobbyCode;
        this.state = LobbyState.WAITING;
        this.host = host;
    }

    public final String getLobbyCode() {
        return this.lobbyCode;
    }

    public final LobbyState getState() {
        return this.state;
    }

    public final Player getHost() {
        return this.host;
    }

    public Collection<Player> getPlayers() {
        return this.players.values();
    }

    public boolean hasPlayer(UUID uuid) {
        return this.players.containsKey(uuid);
    }

    public final boolean addPlayer(Player player) {
        if(this.state != LobbyState.WAITING) return false;
        if(this.players.containsKey(player.getUUID())) return false;

        this.players.put(player.getUUID(), player);
        return true;
    }

    public static Lobby create(Player host) {
        return new Lobby(generateNewLobbyCode(), host);
    }

    private static String generateNewLobbyCode() {
        return "ABCDEF";
    }
}
