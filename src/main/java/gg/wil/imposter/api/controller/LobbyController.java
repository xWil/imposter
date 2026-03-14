package gg.wil.imposter.api.controller;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import gg.wil.imposter.api.messages.LobbyResponse;
import gg.wil.imposter.exception.websocket.InvalidSessionIdException;
import gg.wil.imposter.services.LobbyService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BandwidthBuilder;
import io.github.bucket4j.Bucket;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.UUID;

@RestController
@RequestMapping("/api/lobby")
@CrossOrigin(origins = "http://localhost:3000")
public class LobbyController {

    private static final int TOKEN_COUNT = 5;
    private static final int REFILL_TIME = 10;
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
        Bandwidth bandwidth = BandwidthBuilder.builder().capacity(TOKEN_COUNT)
                .refillGreedy(TOKEN_COUNT, Duration.ofSeconds(REFILL_TIME))
                .initialTokens(TOKEN_COUNT).build();
        return Bucket.builder().addLimit(bandwidth).build();
    }

    private String getClientIP(ServerWebExchange exchange) {
        String header = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (header != null && !header.isEmpty() && !"unknown".equalsIgnoreCase(header)) {
            return header.split(",")[0].trim();
        }

        // fallback to X-Real-IP if X-Forwarded-For is not present
        header = exchange.getRequest().getHeaders().getFirst("X-Real-IP");
        if (header != null && !header.isEmpty() && !"unknown".equalsIgnoreCase(header)) {
            return header;
        }

        // fallback to the direct remote address if no proxy headers are found
        if (exchange.getRequest().getRemoteAddress() != null) {
            return exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        }
        return "unknown";
    }

    @GetMapping("/create")
    public Mono<LobbyResponse> createLobby(ServerWebExchange exchange) {
        Bucket bucket = getBucket(getClientIP(exchange));

        if(bucket.tryConsume(1)) {
            return this.lobbyService.createLobby();
        }
        return Mono.error(new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded"));
    }

    @GetMapping("/join")
    public Mono<LobbyResponse> joinLobby(ServerWebExchange exchange, @RequestParam("lobby") String lobbyCode, @RequestParam("username") String username) {
        Bucket bucket = getBucket(getClientIP(exchange));

        if(bucket.tryConsume(1)) {
            return this.lobbyService.joinLobby(lobbyCode, username);
        }
        return Mono.error(new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded"));
    }

    @GetMapping("/rejoin")
    public Mono<LobbyResponse> rejoinLobby(ServerWebExchange exchange, @RequestParam("session") String sessionID) {
        Bucket bucket = getBucket(getClientIP(exchange));

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
