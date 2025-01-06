package com.dattruongdev.bookstore_cqrs.core.catalog.query;

import com.dattruongdev.bookstore_cqrs.core.catalog.domain.Book;
import com.dattruongdev.bookstore_cqrs.core.catalog.domain.BookRepository;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.HandledBy;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.query.Query;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.query.QueryHandler;
import com.dattruongdev.bookstore_cqrs.response.ApiResponse;
import com.dattruongdev.bookstore_cqrs.response.ErrorResponse;
import com.dattruongdev.bookstore_cqrs.response.IResponse;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.bson.types.ObjectId;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@HandledBy(handler = FindRelatedBooksQueryHandler.class)
public record FindRelatedBooksQuery(String bookId, List<String> categories) implements Query<ResponseEntity<IResponse>> {
}

@Service
@RequiredArgsConstructor
class FindRelatedBooksQueryHandler implements QueryHandler<FindRelatedBooksQuery, ResponseEntity<IResponse>> {
    private final BookRepository bookRepository;


    @Override
    public ResponseEntity<IResponse> handle(FindRelatedBooksQuery query) {
        List<Book> books = bookRepository.findBySameCategoriesNotBookIdInPage(query.categories().stream().map(ObjectId::new).toList(), query.bookId(), PageRequest.of(0, 6)).toList();

        if (books.isEmpty()) {
            return ResponseEntity.status(404).body(new ErrorResponse(404, "Books not found"));
        }



        return ResponseEntity.ok().body(new ApiResponse(
                Map.of(
                        "data", books,
                        "message", "Books found successfully",
                        "status", 200
                )
        ));
    }
}
