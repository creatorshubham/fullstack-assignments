package com.fullstack.bookcatalog.book.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record BookRequest(
        @NotBlank(message = "Title is required.")
        @Size(max = 160, message = "Title must be at most 160 characters.")
        String title,

        @NotBlank(message = "Author is required.")
        @Size(max = 120, message = "Author must be at most 120 characters.")
        String author,

        @NotBlank(message = "ISBN is required.")
        @Pattern(regexp = "^(?:[0-9]{9}[0-9Xx]|[0-9]{13})$",
                message = "ISBN must be a valid 10- or 13-digit ISBN without separators.")
        String isbn,

        @NotBlank(message = "Genre is required.")
        @Size(max = 60, message = "Genre must be at most 60 characters.")
        String genre,

        @Min(value = 1450, message = "Publication year must be 1450 or later.")
        @Max(value = 2100, message = "Publication year must not exceed 2100.")
        int publishedYear
) {
}
