package com.swabhiman.shiftswap.exception;

/**
 * Thrown when an invalid state transition is attempted on a Swap.
 */
public class IllegalStateTransitionException extends SwapException {
    public IllegalStateTransitionException(String message) { super(message); }
}


