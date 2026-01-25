package gg.wil.imposter.api.controller;

import gg.wil.imposter.api.model.CreateRequest;
import gg.wil.imposter.api.model.JoinRequest;
import gg.wil.imposter.api.model.LobbyResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/lobby")
public class LobbyController {

    @GetMapping("/create")
    public Mono<LobbyResponse> createLobby(@RequestBody CreateRequest request) {
        return Mono.just(new LobbyResponse("test", "ws://localhost:8080/ws", UUID.randomUUID().toString()));
    }

    @GetMapping("/join")
    public Mono<LobbyResponse> joinLobby(@RequestBody JoinRequest request) {
        return Mono.just(new LobbyResponse(request.lobbyCode(), "ws://localhost:8080/ws", UUID.randomUUID().toString()));
    }
}
