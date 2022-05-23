package com.example.PKI.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST,reason = "Unexpected value of user role")
public class UnexpectedUserRoleException extends RuntimeException {
}
