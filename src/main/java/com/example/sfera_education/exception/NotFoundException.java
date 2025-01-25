package com.example.sfera_education.exception;

import com.example.sfera_education.payload.ApiResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotFoundException extends RuntimeException {

    private ApiResponse apiResponse;

    public NotFoundException(ApiResponse apiResponse) {
        this.apiResponse = apiResponse;
    }
}
