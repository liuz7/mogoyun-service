package com.mogo.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.util.Date;

@ToString
@Data
@AllArgsConstructor
public class ApiError {

    private HttpStatus status;
    private String error_code;
    private String message;
    private String detail;
    private Date timestamp;

}
