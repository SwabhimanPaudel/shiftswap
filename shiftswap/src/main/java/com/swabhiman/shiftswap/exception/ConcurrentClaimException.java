package com.swabhiman.shiftswap.exception;

/**
 * Indicates two users attempted to claim the same swap concurrently.
 */
public class ConcurrentClaimException extends SwapException {
    public ConcurrentClaimException(String message) { super(message); }
}


