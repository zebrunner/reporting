package com.qaprosoft.zafira.ws.swagger.annotations;

import io.swagger.annotations.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses(value = {@io.swagger.annotations.ApiResponse(code = 200, message = "OK"),
        @io.swagger.annotations.ApiResponse(code = 400, message = "Bad request"),
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not found"),
        @io.swagger.annotations.ApiResponse(code = 500, message = "Server error")})
public @interface ResponseStatusDetails {
}
