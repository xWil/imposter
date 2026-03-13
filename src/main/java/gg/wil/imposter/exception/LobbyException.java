package gg.wil.imposter.exception;

public abstract class LobbyException extends RuntimeException {

    protected final int httpCode;
    protected final LobbyExceptionType type;
    protected final String lobbyCode;

    public LobbyException(String message, LobbyExceptionType type, String lobbyCode, int httpCode) {
        super(message);
        this.type = type;
        this.lobbyCode = lobbyCode;
        this.httpCode = httpCode;
    }

    public final int getHttpCode() {
        return this.httpCode;
    }

    public final LobbyExceptionType getType() {
        return this.type;
    }

    public final String getLobbyCode() {
        return this.lobbyCode;
    }

    public enum LobbyExceptionType {
        ALREADY_IN_LOBBY,
        CANT_CREATE_LOBBY,
        GAME_IN_PROGRESS,
        INVALID_USERNAME,
        LOBBY_FULL,
        LOBBY_NOT_FOUND,
        PLAYER_NOT_ALLOWED;
    }
}
