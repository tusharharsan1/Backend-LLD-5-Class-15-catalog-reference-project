package com.scaler.productcatalogservice.exceptions;

public class AuthServiceUnavailableException extends RuntimeException {

    public AuthServiceUnavailableException(Throwable cause) {
        super("The auth service is not answering", cause);
    }
}
