package com.lacouf.rsbjwt.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthErrorResponse {
    private String message;
    private String errorCode;
}
