package gg.wil.imposter.api.controller;

import gg.wil.imposter.api.model.CreateRequest;
import gg.wil.imposter.api.model.JoinRequest;
import gg.wil.imposter.api.model.LobbyResponse;
import gg.wil.imposter.services.LobbyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/lobby")
public class LobbyController {

    private final LobbyService lobbyService;

    public LobbyController(LobbyService lobbyService) {
        this.lobbyService = lobbyService;
    }

    @GetMapping("/create")
    public Mono<LobbyResponse> createLobby(@RequestBody CreateRequest request) {
        return this.lobbyService.createLobby(request.username());
    }

    @GetMapping("/join")
    public Mono<LobbyResponse> joinLobby(@RequestBody JoinRequest request) {
        return this.lobbyService.joinLobby(request.lobbyCode(), request.username());
    }
}
