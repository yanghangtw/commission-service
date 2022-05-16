package com.thoughtworks.commissionservice.service;

public enum ErrorMessage {
    ORDER_NOT_RECEIVED("ORDER_NOT_RECEIVED"),
    COMMISSION_ALREADY_ACQUIRED("COMMISSION_ALREADY_ACQUIRED"),
    ORDER_NOT_FOUND("ORDER_NOT_FOUND"),
    RETRIEVE_ORDER_FAILED("RETRIEVE_ORDER_FAILED");

    String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
