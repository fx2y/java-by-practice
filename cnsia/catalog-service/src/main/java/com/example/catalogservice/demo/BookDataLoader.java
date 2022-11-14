package com.example.catalogservice.demo;

import com.example.catalogservice.domain.Book;
import com.example.catalogservice.domain.BookRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Profile("testdata")
public class BookDataLoader {
    private final BookRepository bookRepository;

    public BookDataLoader(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void loadBookTestData() {
        var book1 = new Book("9783161484100", "The Hitchhiker's Guide to the Galaxy", "Douglas Adams", 10.00);
        var book2 = new Book("9780596007126", "The Hitchhiker's Guide to the Galaxy 2", "Douglas Adams 2", 15.00);
        bookRepository.save(book1);
        bookRepository.save(book2);
    }

}
