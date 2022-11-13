package com.example.catalogservice.domain;


import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BookValidationTests {
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenAllFieldsCorrectThenValidationSucceeds() {
        var bookIsbn = "9783161484100";
        var book = new Book(bookIsbn, "The Hitchhiker's Guide to the Galaxy", "Douglas Adams", 10.00);
        var violations = validator.validate(book);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenIsbnNotDefinedThenValidationFails() {
        var book = new Book("", "The Hitchhiker's Guide to the Galaxy", "Douglas Adams", 10.00);
        var violations = validator.validate(book);
        assertThat(violations).hasSize(2);
        var constraintViolationMessages = violations.stream().map(ConstraintViolation::getMessage).toList();
        assertThat(constraintViolationMessages).containsExactlyInAnyOrder(
                "The Book ISBN must be defined.",
                "The ISBN format must be valid."
        );
    }
}
