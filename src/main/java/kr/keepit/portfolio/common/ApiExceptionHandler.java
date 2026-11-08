package kr.keepit.portfolio.common;

import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<?> invalid(MethodArgumentNotValidException e) {
        var error = e.getBindingResult().getFieldErrors().getFirst();
        return ResponseEntity.badRequest().body(Map.of("message", error.getField() + ": " + error.getDefaultMessage()));
    }

    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<?> status(ResponseStatusException e) {
        return ResponseEntity.status(e.getStatusCode()).body(Map.of("message", e.getReason() == null ? "요청을 처리할 수 없어요." : e.getReason()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<?> unreadable() {
        return ResponseEntity.badRequest().body(Map.of("message", "날짜와 경험 종류를 확인해주세요."));
    }
}
