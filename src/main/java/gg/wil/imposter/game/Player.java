package gg.wil.imposter.game;

import java.util.UUID;

public class Player {

    private final UUID uuid;
    private final String username;

    private Player(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public String getUsername() {
        return this.username;
    }

    public static Player create(String username) {
        return new Player(UUID.randomUUID(), username);
    }
}
