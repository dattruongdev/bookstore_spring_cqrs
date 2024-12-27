package com.dattruongdev.bookstore_cqrs.response;

import java.util.Map;

public record ApiResponse(Map<String, Object> response) implements IResponse{
}
