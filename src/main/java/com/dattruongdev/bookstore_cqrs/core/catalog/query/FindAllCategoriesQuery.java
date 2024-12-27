package com.dattruongdev.bookstore_cqrs.core.catalog.query;

import com.dattruongdev.bookstore_cqrs.core.catalog.domain.Category;
import com.dattruongdev.bookstore_cqrs.core.catalog.domain.CategoryRepository;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.HandledBy;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.query.Query;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.query.QueryHandler;
import com.dattruongdev.bookstore_cqrs.response.ApiResponse;
import com.dattruongdev.bookstore_cqrs.response.ErrorResponse;
import com.dattruongdev.bookstore_cqrs.response.IResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@HandledBy(handler = FindAllCategoriesQueryHandler.class)
public record FindAllCategoriesQuery() implements Query<ResponseEntity<IResponse>> {
}

@Service
@RequiredArgsConstructor
class FindAllCategoriesQueryHandler implements QueryHandler<FindAllCategoriesQuery, ResponseEntity<IResponse>> {
    private final CategoryRepository categoryRepository;

    @Override
    public ResponseEntity<IResponse> handle(FindAllCategoriesQuery query) {
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            return ResponseEntity.ok().body(new ErrorResponse(404, "No categories found"));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", 200);
        response.put("message", "Categories found");
        response.put("data", categories);

        return ResponseEntity.ok().body(new ApiResponse(response));
    }
}
