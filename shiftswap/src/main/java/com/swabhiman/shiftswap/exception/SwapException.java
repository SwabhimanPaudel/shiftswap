package com.swabhiman.shiftswap.exception;

/**
 * General-purpose business exception for swap operations.
 */
public class SwapException extends RuntimeException {
    public SwapException(String message) { super(message); }
    public SwapException(String message, Throwable cause) { super(message, cause); }
}


