package com.fullstack.bookcatalog.book.service;

import com.fullstack.bookcatalog.book.dto.BookRequest;
import com.fullstack.bookcatalog.book.dto.BookResponse;
import com.fullstack.bookcatalog.book.exception.BookNotFoundException;
import com.fullstack.bookcatalog.book.exception.DuplicateIsbnException;
import com.fullstack.bookcatalog.book.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class BookServiceIntegrationTests {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void clearBooks() {
        bookRepository.deleteAll();
    }

    @Test
    void createsReadsUpdatesAndDeletesBooks() {
        BookResponse created = bookService.create(book("The Left Hand of Darkness", "Ursula K. Le Guin",
                "9780441478125", "Science Fiction", 1969));

        assertEquals("The Left Hand of Darkness", bookService.getById(created.id()).title());
        assertEquals(1, bookService.getAll(PageRequest.of(0, 10)).getTotalElements());

        BookResponse updated = bookService.update(created.id(), book("The Dispossessed", "Ursula K. Le Guin",
                "9780061054884", "Science Fiction", 1974));
        assertEquals("The Dispossessed", updated.title());

        bookService.delete(created.id());
        assertThrows(BookNotFoundException.class, () -> bookService.getById(created.id()));
    }

    @Test
    void rejectsDuplicateIsbn() {
        bookService.create(book("Dune", "Frank Herbert", "9780441172719", "Science Fiction", 1965));

        assertThrows(DuplicateIsbnException.class,
                () -> bookService.create(book("Another Dune", "Frank Herbert",
                        "9780441172719", "Science Fiction", 1965)));
    }

    @Test
    void searchesAndFiltersWithPaginationAndSorting() {
        bookService.create(book("Dune", "Frank Herbert", "9780441172719", "Science Fiction", 1965));
        bookService.create(book("Children of Dune", "Frank Herbert", "9780425077714",
                "Science Fiction", 1976));
        bookService.create(book("Pride and Prejudice", "Jane Austen", "9780141439518", "Classic", 1813));

        var searchResults = bookService.search("dune",
                PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, "title")));
        var genreResults = bookService.getByGenre("classic", PageRequest.of(0, 10));

        assertEquals(2, searchResults.getTotalElements());
        assertEquals(1, searchResults.getContent().size());
        assertEquals("Children of Dune", searchResults.getContent().get(0).title());
        assertEquals("Pride and Prejudice", genreResults.getContent().get(0).title());
    }

    private BookRequest book(String title, String author, String isbn, String genre, int year) {
        return new BookRequest(title, author, isbn, genre, year);
    }
}
