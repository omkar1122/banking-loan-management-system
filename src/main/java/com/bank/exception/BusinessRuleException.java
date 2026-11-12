package com.bank.exception;

/** Thrown when a banking/loan business rule is violated. Rolls back the surrounding transaction. */
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) { super(message); }
}
