package com.dattruongdev.bookstore_cqrs.response;

public record ErrorResponse(int errorCode, String message) implements  IResponse {
}
