package com.dattruongdev.bookstore_cqrs.response;

public record ApiResponse(int statusCode, String message, Object data) implements IResponse{
}
