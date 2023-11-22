package com.test.crypto.common.exceptions.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiError {
    private Integer code;
    private String message;
}

