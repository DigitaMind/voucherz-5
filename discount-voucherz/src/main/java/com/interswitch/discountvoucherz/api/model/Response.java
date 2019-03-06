package com.interswitch.discountvoucherz.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.interswitch.discountvoucherz.api.model.exception.Error;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@ToString
public class Response{
    private final Timestamp timestamp;

    private final Integer status;

    @JsonProperty("Http Status Phrase")
    private final HttpStatus httpStatus;

    private final String message;

    private final List<Error> errors;

    public Response(HttpStatus httpStatus, String message, List<Error> errors) {
        this.timestamp = timestamp();
        this.status = httpStatus.value();
        this.httpStatus = httpStatus;
        this.message = message;
        this.errors = errors;
    }

    public Timestamp timestamp(){
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        return timestamp;
    }
}
