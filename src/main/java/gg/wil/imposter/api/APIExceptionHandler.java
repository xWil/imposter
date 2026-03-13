package gg.wil.imposter.api;

import gg.wil.imposter.api.messages.ErrorResponse;
import gg.wil.imposter.exception.LobbyException;
import gg.wil.imposter.exception.WebSocketException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class APIExceptionHandler {

    @ExceptionHandler(LobbyException.class)
    public ResponseEntity<?> handleLobbyException(LobbyException e) {
        return ResponseEntity.status(e.getHttpCode()).body(new ErrorResponse(e.getType().toString(), e.getMessage()));
    }

    @ExceptionHandler(WebSocketException.class)
    public ResponseEntity<?> handleWebSocketException(WebSocketException e) {
        return ResponseEntity.status(409).body(new ErrorResponse(e.getType().toString(), e.getMessage()));
    }
}
