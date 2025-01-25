package com.example.sfera_education.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class ResponseError {

    private int code;
    private String message;

    public static ResponseError ACCESS_DENIED() {
        return new ResponseError(1, "Kirish taqiqlanadi.");
    }

    public static ResponseError NOTFOUND(Object data) {
        return new ResponseError(2, String.format("%s topilmadi.", data));
    }

    public static ResponseError PASSWORD_DID_NOT_MATCH() {
        return new ResponseError(3, "Telefon nomer yoki Parol mos kelmadi.");
    }

    public static ResponseError ALREADY_EXIST(String data) {
        return new ResponseError(4, String.format("%s allaqachon mavjud.", data));
    }

    public static ResponseError VALIDATION_FAILED(String message) {
        return new ResponseError(5, message);
    }

    public static ResponseError YOR_ARE_NOT_AN_ADMIN() {
        return new ResponseError(5, "Siz admin emassiz!");
    }

    public static ResponseError DEFAULT_ERROR(String message) {
        return new ResponseError(7, message);
    }

    public static ResponseError SERVER_ERROR(String message) {
        return new ResponseError(8, message);
    }
}
