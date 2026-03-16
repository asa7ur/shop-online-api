package org.alixar.api.dtos;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDTO<T> {
    private LocalDateTime timestamp;
    private int status;
    private String message;
    private FilterDTO filter;
    private T data;

    public static <T> ResponseDTO<T> success(String message, T data) {
        return new ResponseDTO<>(LocalDateTime.now(), 200, message, null, data);
    }

    public static <T> ResponseDTO<T> success(String message, T data, FilterDTO filter) {
        return new ResponseDTO<>(LocalDateTime.now(), 200, message, filter, data);
    }

    public static <T> ResponseDTO<T> error(int status, String message) {
        return new ResponseDTO<>(LocalDateTime.now(), status, message, null, null);
    }
}
