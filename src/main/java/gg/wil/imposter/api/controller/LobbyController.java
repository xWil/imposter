package gg.wil.imposter.api.controller;

import gg.wil.imposter.api.messages.CreateRequest;
import gg.wil.imposter.api.messages.JoinRequest;
import gg.wil.imposter.api.messages.LobbyResponse;
import gg.wil.imposter.services.LobbyService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/lobby")
public class LobbyController {

    private final LobbyService lobbyService;

    public LobbyController(LobbyService lobbyService) {
        this.lobbyService = lobbyService;
    }

    @PostMapping("/create")
    public Mono<LobbyResponse> createLobby(@RequestBody CreateRequest request) {
        return this.lobbyService.createLobby(request.username());
    }

    @PostMapping("/join")
    public Mono<LobbyResponse> joinLobby(@RequestBody JoinRequest request) {
        return this.lobbyService.joinLobby(request.lobbyCode(), request.username());
    }
}
