package gg.wil.imposter.api.messages.websocket;

import gg.wil.imposter.api.messages.websocket.send.*;
import gg.wil.imposter.api.messages.websocket.send.state.SendGameStateMessage;

public enum WebSocketSendMessageType {
    ANSWERING_START(SendAnsweringStartMessage.class),
    ANSWERS(SendAnswersMessage.class),
    GAME_END(SendGameEndMessage.class),
    GAME_START(SendGameStartMessage.class),
    GAME_START_ERROR(SendGameStartErrorMessage.class),
    GAME_STATE(SendGameStateMessage.class),
    HOST_LEAVE(SendHostLeaveMessage.class),
    ICON_CHANGE(SendIconChangeMessage.class),
    PLAYER_FINISHED_ANSWERING(SendPlayerFinishedAnsweringMessage.class),
    PLAYER_JOIN(SendPlayerJoinMessage.class),
    PLAYER_LEAVE(SendPlayerLeaveMessage.class),
    PLAYER_LIST(SendPlayerListMessage.class),
    QUESTION(SendQuestionMessage.class),
    TIMES_UP(SendTimesUpMessage.class),
    SCORES(SendScoresMessage.class),
    VOTING_START(SendVotingStartMessage.class),
    VOTES(SendVotesMessage.class);

    private final Class<?> messageClass;
    public Class<?> getMessageClass() { return messageClass; }

    WebSocketSendMessageType(Class<?> messageClass) {
        this.messageClass = messageClass;
    }
}
