package com.mogo.exception.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BuildPackNotFoundException extends RuntimeException {

    private String code;
    private String msg;

    public BuildPackNotFoundException(String msg) {
        super(msg);
        this.msg = msg;
    }
}
