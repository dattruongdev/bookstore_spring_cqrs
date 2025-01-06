package com.dattruongdev.bookstore_cqrs.core.catalog.command;

import com.dattruongdev.bookstore_cqrs.core.catalog.domain.Review;
import com.dattruongdev.bookstore_cqrs.core.catalog.domain.ReviewRepository;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.HandledBy;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.command.Command;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.command.CommandHandler;
import com.dattruongdev.bookstore_cqrs.response.ApiResponse;
import com.dattruongdev.bookstore_cqrs.response.ErrorResponse;
import com.dattruongdev.bookstore_cqrs.response.IResponse;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@HandledBy(handler = LeaveReviewCommandHandler.class)
public record LeaveReviewCommand(double rating, String name, String email, String content, String bookId) implements Command<ResponseEntity<IResponse>> {
}

@Service
@RequiredArgsConstructor
class LeaveReviewCommandHandler implements CommandHandler<LeaveReviewCommand, ResponseEntity<IResponse>> {
    private final ReviewRepository reviewRepository;
    public ResponseEntity<IResponse> handle(LeaveReviewCommand command) {
        Review review = new Review();
        review.setContent(command.content());
        review.setUsername(command.name());
        review.setEmail(command.email());
        review.setRating(command.rating());
        review.setBookId(new ObjectId(command.bookId()));
        review.setUpdatedAt(new Date());
        review.setCreatedAt(new Date());

        try {
            review = reviewRepository.save(review);
            return ResponseEntity.status(201).body(new ApiResponse(Map.of(
                    "status", 201,
                    "message", "Review has been added successfully",
                    "data", review
            )));
        } catch(DataAccessException e) {
            return ResponseEntity.internalServerError().body(new ErrorResponse(500, e.getMessage()));
        }


    }
}
