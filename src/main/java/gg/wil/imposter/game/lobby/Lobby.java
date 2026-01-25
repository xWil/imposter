package gg.wil.imposter.game.lobby;

import gg.wil.imposter.exception.LobbyException;
import gg.wil.imposter.exception.lobby.AlreadyInLobbyException;
import gg.wil.imposter.exception.lobby.InProgressException;
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

    public final void addPlayer(Player player) throws LobbyException {
        if(this.state != LobbyState.WAITING) throw new InProgressException(this.lobbyCode);
        if(this.players.containsKey(player.getUUID())) throw new AlreadyInLobbyException(this.lobbyCode);

        this.players.put(player.getUUID(), player);
    }

    public static Lobby create(Player host) {
        return new Lobby(generateNewLobbyCode(), host);
    }

    private static String generateNewLobbyCode() {
        return "ABCDEF";
    }
}
