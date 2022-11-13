package com.example.catalogservice.web;

import com.example.catalogservice.domain.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookJsonTests {
    @Autowired
    private JacksonTester<Book> json;

    @Test
    void testSerialize() throws Exception {
        var book = new Book("9783161484100", "The Hitchhiker's Guide to the Galaxy", "Douglas Adams", 10.00);
        var jsonContent = json.write(book);
        assertThat(jsonContent).extractingJsonPathStringValue("@.isbn").isEqualTo(book.isbn());
        assertThat(jsonContent).extractingJsonPathStringValue("@.title").isEqualTo(book.title());
        assertThat(jsonContent).extractingJsonPathStringValue("@.author").isEqualTo(book.author());
        assertThat(jsonContent).extractingJsonPathNumberValue("@.price").isEqualTo(book.price());
    }

    @Test
    void testDeserialize() throws Exception {
        var content = """
                {
                    "isbn": "9783161484100",
                    "title": "The Hitchhiker's Guide to the Galaxy",
                    "author": "Douglas Adams",
                    "price": 10.00
                }
                """;
        assertThat(json.parse(content))
                .usingRecursiveComparison()
                .isEqualTo(new Book("9783161484100", "The Hitchhiker's Guide to the Galaxy", "Douglas Adams", 10.00));
    }
}
