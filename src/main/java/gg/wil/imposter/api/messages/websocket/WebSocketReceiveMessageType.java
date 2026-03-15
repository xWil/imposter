package gg.wil.imposter.api.messages.websocket;

import com.google.gson.JsonObject;
import gg.wil.imposter.api.messages.websocket.receive.*;
import gg.wil.imposter.exception.MessageException;
import gg.wil.imposter.game.Player;

public enum WebSocketReceiveMessageType {
    ANSWER_SUBMIT(ReceiveAnswerSubmitMessage::new),
    GAME_END(ReceiveGameEndMessage::new),
    GAME_START(ReceiveGameStartMessage::new),
    ICON_CHANGE(ReceiveIconChangeMessage::new),
    INTRO_FINISHED(ReceiveIntroFinishedMessage::new),
    PING(ReceivePingMessage::new),
    PHASE_END(ReceivePhaseEndMessage::new),
    PLAYER_JOIN(ReceivePlayerJoinMessage::new),
    PLAYER_LEAVE(ReceivePlayerLeaveMessage::new),
    PLAYER_REJOIN(ReceivePlayerRejoinMessage::new),
    ROUND_END(ReceiveRoundEndMessage::new),
    SCORES_GET(ReceiveScoresGetMessage::new),
    VOTE_SUBMIT(ReceiveVoteSubmitMessage::new);

    private final ReceiveMessageFactory factory;
    public WebSocketReceiveMessage create(Player from, JsonObject json) throws MessageException { return factory.create(from, json); }

    WebSocketReceiveMessageType(ReceiveMessageFactory factory) {
        this.factory = factory;
    }

    private interface ReceiveMessageFactory {
        WebSocketReceiveMessage create(Player from, JsonObject json) throws MessageException;
    }
}