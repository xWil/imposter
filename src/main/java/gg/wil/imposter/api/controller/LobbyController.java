package gg.wil.imposter.api.controller;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import gg.wil.imposter.Config;
import gg.wil.imposter.api.messages.LobbyResponse;
import gg.wil.imposter.exception.websocket.InvalidSessionIdException;
import gg.wil.imposter.services.LobbyService;
import gg.wil.imposter.util.ImposterUtil;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BandwidthBuilder;
import io.github.bucket4j.Bucket;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.UUID;

@RestController
@RequestMapping("/api/lobby")
@CrossOrigin(origins = "${app.cors.allowed-origins}")
@ConditionalOnExpression("'${app.server.mode}'.toUpperCase() == 'PROXY' || '${app.server.mode}'.toUpperCase() == 'BOTH'")
public class LobbyController {

    private final LobbyService lobbyService;

    private final Cache<String, Bucket> cache = Caffeine.newBuilder()
            .expireAfterAccess(Duration.ofMinutes(2))
            .maximumSize(10_000).build();

    public LobbyController(LobbyService lobbyService) {
        this.lobbyService = lobbyService;
    }

    private Bucket getBucket(String ip) {
        return this.cache.get(ip, _ -> buildBucket());
    }

    private Bucket buildBucket() {
        Bandwidth bandwidth = BandwidthBuilder.builder().capacity(Config.API_TOKEN_COUNT)
                .refillGreedy(Config.API_TOKEN_COUNT, Duration.ofSeconds(Config.API_REFILL_TIME))
                .initialTokens(Config.API_TOKEN_COUNT).build();
        return Bucket.builder().addLimit(bandwidth).build();
    }

    @GetMapping("/create")
    public Mono<LobbyResponse> createLobby(ServerWebExchange exchange) {
        Bucket bucket = getBucket(ImposterUtil.getClientIP(exchange));

        if(bucket.tryConsume(1)) {
            return this.lobbyService.createLobby();
        }
        return Mono.error(new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded"));
    }

    @GetMapping("/join")
    public Mono<LobbyResponse> joinLobby(ServerWebExchange exchange, @RequestParam("lobby") String lobbyCode, @RequestParam("username") String username) {
        Bucket bucket = getBucket(ImposterUtil.getClientIP(exchange));

        if(bucket.tryConsume(1)) {
            return this.lobbyService.joinLobby(lobbyCode, username);
        }
        return Mono.error(new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded"));
    }

    @GetMapping("/rejoin")
    public Mono<LobbyResponse> rejoinLobby(ServerWebExchange exchange, @RequestParam("session") String sessionID) {
        Bucket bucket = getBucket(ImposterUtil.getClientIP(exchange));

        if(bucket.tryConsume(1)) {
            UUID session = null;
            try {
                session = UUID.fromString(sessionID);
            } catch(IllegalArgumentException e) {
                return Mono.error(new InvalidSessionIdException());
            }
            return this.lobbyService.rejoinLobby(session);
        }
        return Mono.error(new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded"));
    }
}
