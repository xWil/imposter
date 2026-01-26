package gg.wil.imposter.api;

import gg.wil.imposter.api.messages.ErrorResponse;
import gg.wil.imposter.exception.LobbyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class APIExceptionHandler {

    @ExceptionHandler(LobbyException.class)
    public ResponseEntity<?> handleLobbyException(LobbyException e) {
        return ResponseEntity.status(e.getHttpCode()).body(new ErrorResponse(e.getType().toString(), e.getMessage()));
    }
}
