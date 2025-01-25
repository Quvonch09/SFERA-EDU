package com.example.sfera_education.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse {
    private Object data;
    private ResponseError error;

    public ApiResponse(Object data) {
        this.data = data;
    }

    public ApiResponse(ResponseError error) {
        this.error = error;
    }
}
