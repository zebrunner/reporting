package com.qaprosoft.zafira.ws.annotations;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses(value = {@ApiResponse(code = 200, message = "Successful"),
        @ApiResponse(code = 404, message = "Entity is already exists"),
        @ApiResponse(code = 400, message = "Entity is already exists"),
        @ApiResponse(code = 500, message = "Server error")})
public @interface PostResponse {
}
