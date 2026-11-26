
package com.cartera.backend.exception;

public class ConflictException extends RuntimeException {

    public ConflictException(String mensaje) {

        super(mensaje);
    }
}

