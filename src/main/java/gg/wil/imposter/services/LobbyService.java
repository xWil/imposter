package gg.wil.imposter.services;

import gg.wil.imposter.api.model.LobbyResponse;
import gg.wil.imposter.game.Player;
import gg.wil.imposter.game.lobby.Lobby;
import gg.wil.imposter.repo.LobbyRepo;
import org.springframework.stereotype.Service;

@Service
public class LobbyService {

    private final LobbyRepo lobbyRepo;

    public LobbyService(LobbyRepo lobbyRepo) {
        this.lobbyRepo = lobbyRepo;
    }

    public final LobbyResponse createLobby(String username) {
        Player host = Player.create(username);
        Lobby lobby = Lobby.create(host);
        lobbyRepo.addLobby(lobby);
        return new LobbyResponse(lobby.getLobbyCode(), "ws://localhost:8080/ws", host.getUUID().toString());
    }

    public final LobbyResponse joinLobby(String lobbyCode, String username) {
        Lobby lobby = lobbyRepo.getLobby(lobbyCode);
        if(lobby == null) return null;
        Player player = Player.create(username);
        return new LobbyResponse(lobbyCode, "ws://localhost:8080/ws", player.getUUID().toString());
    }
}
