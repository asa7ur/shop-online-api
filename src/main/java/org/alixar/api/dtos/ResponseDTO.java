package org.alixar.api.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ResponseDTO<T> {
    private LocalDateTime timestamp;
    private int status;
    private String message;
    private FilterDTO filter;
    private T data;

    public ResponseDTO(int status, String message, T data, FilterDTO filter) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.message = message;
        this.data = data;
        this.filter = filter;
    }

    public ResponseDTO(int status, String message, T data) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.message = message;
        this.data = data;
    }
}
