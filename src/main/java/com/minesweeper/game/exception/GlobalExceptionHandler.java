package com.minesweeper.game.exception;

import com.minesweeper.game.model.Response.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        APIResponse<Map<String, String>> response = APIResponse.error(400, "参数验证失败");
        response.setData(errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 处理IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<APIResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        APIResponse<Void> response = APIResponse.error(400, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 处理IllegalStateException
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<APIResponse<Void>> handleIllegalStateException(IllegalStateException ex) {
        APIResponse<Void> response = APIResponse.error(409, ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    /**
     * 处理所有其他异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponse<Void>> handleGenericException(Exception ex) {
        APIResponse<Void> response = APIResponse.error(500, "服务器内部错误: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

