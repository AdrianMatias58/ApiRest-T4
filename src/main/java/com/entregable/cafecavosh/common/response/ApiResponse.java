package com.entregable.cafecavosh.common.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private Boolean succes;
    private String message;
    private Instant time;
    private List<String> errors;
    private  T data;

    public static <T> ApiResponse <T> sucess(String message, T data){
        ApiResponse<T> response = new ApiResponse<>();
        response.succes = true;
        response.message = message;
        response.time = Instant.now();
        response.data = data;
        return response;
    }
    public static <T> ApiResponse <T> error(String message, List<String> errors){
        ApiResponse<T> response = new ApiResponse<>();
        response.succes = false;
        response.message = message;
        response.errors = errors;
        response.time = Instant.now();
        return response;
    }
}
