package com.example.catalogservice;

import com.example.catalogservice.domain.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CatalogServiceApplicationTests {
    @Autowired
    private WebTestClient webTestClient;

    @Test
    void whenGetRequestWithIdThenBookReturned() {
        var bookIsbn = "9783161484100";
        var bookToCreate = new Book(bookIsbn, "The Hitchhiker's Guide to the Galaxy", "Douglas Adams", 10.00);
        Book expectedBook = webTestClient.post()
                .uri("/books")
                .bodyValue(bookToCreate)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Book.class).value(book -> assertThat(book).isNotNull())
                .returnResult().getResponseBody();

        webTestClient.get()
                .uri("/books/" + bookIsbn)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(Book.class).value(book -> {
                    assertThat(book).isNotNull();
                    assert expectedBook != null;
                    assertThat(book.isbn()).isEqualTo(expectedBook.isbn());
                });
    }

    @Test
    void whenPostRequestThenBookCreated() {
        var bookIsbn = "9783161484100";
        var expectedBook = new Book(bookIsbn, "The Hitchhiker's Guide to the Galaxy", "Douglas Adams", 10.00);

        webTestClient.post()
                .uri("/books")
                .bodyValue(expectedBook)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Book.class).value(book -> {
                    assertThat(book).isNotNull();
                    assertThat(book.isbn()).isEqualTo(bookIsbn);
                });
    }

    @Test
    void whenPutRequestThenBookUpdated() {
        var bookIsbn = "9783161484100";
        var bookToCreate = new Book(bookIsbn, "The Hitchhiker's Guide to the Galaxy", "Douglas Adams", 10.00);
        Book expectedBook = webTestClient.post()
                .uri("/books")
                .bodyValue(bookToCreate)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Book.class).value(book -> assertThat(book).isNotNull())
                .returnResult().getResponseBody();

        var bookToUpdate = new Book(bookIsbn, "The Hitchhiker's Guide to the Galaxy", "Douglas Adams", 20.00);
        webTestClient.put()
                .uri("/books/" + bookIsbn)
                .bodyValue(bookToUpdate)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Book.class).value(book -> {
                    assertThat(book).isNotNull();
                    assert expectedBook != null;
                    assertThat(book.isbn()).isEqualTo(expectedBook.isbn());
                    assertThat(book.price()).isEqualTo(bookToUpdate.price());
                });
    }

    @Test
    void whenDeleteRequestThenBookDeleted() {
        var bookIsbn = "9783161484100";
        var bookToCreate = new Book(bookIsbn, "The Hitchhiker's Guide to the Galaxy", "Douglas Adams", 10.00);
        webTestClient.post()
                .uri("/books")
                .bodyValue(bookToCreate)
                .exchange()
                .expectStatus().isCreated();

        webTestClient.delete()
                .uri("/books/" + bookIsbn)
                .exchange()
                .expectStatus().isNoContent();

        webTestClient.get()
                .uri("/books/" + bookIsbn)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class).value(errorMessage -> assertThat(errorMessage).isEqualTo("The book with ISBN " + bookIsbn + " was not found."));
    }
}
