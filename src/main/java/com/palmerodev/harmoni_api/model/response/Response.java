package com.palmerodev.harmoni_api.model.response;

public record Response<T>(Integer statusCode, String statusMessage, T data){
}
