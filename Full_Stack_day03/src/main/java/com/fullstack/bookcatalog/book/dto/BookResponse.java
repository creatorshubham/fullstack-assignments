package com.fullstack.bookcatalog.book.dto;

public record BookResponse(
        Long id,
        String title,
        String author,
        String isbn,
        String genre,
        int publishedYear
) {
}
