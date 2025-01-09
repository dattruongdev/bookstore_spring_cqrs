package com.dattruongdev.bookstore_cqrs.core.catalog.eventlisteners;

import com.dattruongdev.bookstore_cqrs.core.catalog.domain.BookRepository;
import com.dattruongdev.bookstore_cqrs.core.catalog.domain.Copy;
import com.dattruongdev.bookstore_cqrs.core.catalog.domain.CopyRepository;
import com.dattruongdev.bookstore_cqrs.core.transaction.events.BookSoldEvent;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BookSoldEventListener {
    private final CopyRepository copyRepository;

    @EventListener
    public void handleBookSoldEvent(BookSoldEvent bookSold) {
        List<Copy> copies = copyRepository.findAllById(bookSold.copyIds().stream().map(ObjectId::toString).toList());
        copies.forEach(Copy::makeUnavailable);
        bookSold.book().setCopies(copies.stream().map(Copy::getId).toList());

        copyRepository.saveAll(copies);
    }
}
