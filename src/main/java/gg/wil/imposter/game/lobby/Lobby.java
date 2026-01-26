package gg.wil.imposter.game.lobby;

import gg.wil.imposter.exception.LobbyException;
import gg.wil.imposter.exception.lobby.AlreadyInLobbyException;
import gg.wil.imposter.exception.lobby.InProgressException;
import gg.wil.imposter.game.Player;
import gg.wil.imposter.repo.LobbyRepo;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Lobby {

    private final String lobbyCode;
    private LobbyState state;
    private final Player host;
    private final ConcurrentHashMap<UUID, Player> players = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, WebSocketSession> sessions = new ConcurrentHashMap<>();

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

    public final void playerConnected(UUID playerID, WebSocketSession session) {
        if(!this.players.containsKey(playerID)) return;
        if(this.sessions.containsKey(playerID)) return;

        this.sessions.put(playerID, session);
    }

    public final void playerDisconnected(UUID playerID) {
        sessions.remove(playerID);
    }

    public final Mono<Void> receiveMessage(UUID playerID, String message) {
        if(!sessions.containsKey(playerID)) return Mono.empty();
        System.out.println(message);
        return Mono.empty();
    }

    ///  STATIC

    private static final char[] allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray();

    public static Lobby create(Player host) {
        String code = generateNewLobbyCode();
        if(code == null) return null;
        return new Lobby(generateNewLobbyCode(), host);
    }

    private static String generateNewLobbyCode() {
        boolean success = false;
        StringBuilder code = new StringBuilder();
        int attempts = 0;
        while(!success) {
            if(attempts >= 100) return null;
            attempts++;

            code = new StringBuilder();
            for(int i = 0; i < 6; i++) {
                code.append(allowedChars[(int) (Math.random() * allowedChars.length)]);
            }
            success = LobbyRepo.getInstance().getLobby(code.toString()) == null;
        }
        return code.toString();
    }
}
