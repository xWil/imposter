package gg.wil.imposter.api.controller;

import gg.wil.imposter.api.messages.LobbyResponse;
import gg.wil.imposter.services.LobbyService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/lobby")
@CrossOrigin(origins = "http://localhost:3000")
public class LobbyController {

    private final LobbyService lobbyService;

    public LobbyController(LobbyService lobbyService) {
        this.lobbyService = lobbyService;
    }

    @GetMapping("/create")
    public Mono<LobbyResponse> createLobby() {
        return this.lobbyService.createLobby();
    }

    @GetMapping("/join")
    public Mono<LobbyResponse> joinLobby(@RequestParam("lobby") String lobbyCode, @RequestParam("username") String username) {
        return this.lobbyService.joinLobby(lobbyCode, username);
    }

    @GetMapping("/rejoin")
    public Mono<LobbyResponse> rejoinLobby(@RequestParam("lobby") String lobbyCode, @RequestParam("playerID") String playerID) {
        return this.lobbyService.rejoinLobby(lobbyCode, UUID.fromString(playerID));
    }
}
