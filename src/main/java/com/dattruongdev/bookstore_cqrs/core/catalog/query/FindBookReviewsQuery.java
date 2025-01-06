package com.dattruongdev.bookstore_cqrs.core.catalog.query;

import com.dattruongdev.bookstore_cqrs.core.catalog.domain.Review;
import com.dattruongdev.bookstore_cqrs.core.catalog.domain.ReviewRepository;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.Dispatchable;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.HandledBy;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.query.Query;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.query.QueryHandler;
import com.dattruongdev.bookstore_cqrs.response.ApiResponse;
import com.dattruongdev.bookstore_cqrs.response.ErrorResponse;
import com.dattruongdev.bookstore_cqrs.response.IResponse;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@HandledBy(handler = FindBookReviewsQueryHandler.class)
public record FindBookReviewsQuery(String bookId, int page, int size) implements Query<ResponseEntity<IResponse>> {
}

@Service
@RequiredArgsConstructor
class FindBookReviewsQueryHandler implements QueryHandler<FindBookReviewsQuery,ResponseEntity<IResponse>> {
    private final ReviewRepository reviewRepository;
    @Override
    public ResponseEntity<IResponse> handle(FindBookReviewsQuery query) {
        List<Review> reviews = reviewRepository.findByBookIdOrderByCreatedAtDesc(new ObjectId(query.bookId()), PageRequest.of(query.page(), query.size())).toList();

        if (reviews.isEmpty()) {
            return ResponseEntity.status(404).body(new ErrorResponse(404, "No reviews found"));
        }

        return ResponseEntity.ok().body(new ApiResponse(Map.of(
                "status", 200,
                "message", "Reviews found",
                "data", reviews,
                "count", reviews.size(),
                "totalInBook", reviewRepository.countByBookId(new ObjectId(query.bookId()))
        )));
    }
}
