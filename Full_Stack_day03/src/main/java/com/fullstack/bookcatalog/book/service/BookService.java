package com.fullstack.bookcatalog.book.service;

import com.fullstack.bookcatalog.book.dto.BookRequest;
import com.fullstack.bookcatalog.book.dto.BookResponse;
import com.fullstack.bookcatalog.book.entity.Book;
import com.fullstack.bookcatalog.book.exception.BookNotFoundException;
import com.fullstack.bookcatalog.book.exception.DuplicateIsbnException;
import com.fullstack.bookcatalog.book.repository.BookRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public BookResponse create(BookRequest request) {
        ensureIsbnAvailable(request.isbn(), null);
        return toResponse(bookRepository.save(toEntity(request)));
    }

    @Transactional(readOnly = true)
    public Page<BookResponse> getAll(Pageable pageable) {
        return bookRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public BookResponse getById(Long id) {
        return toResponse(findBook(id));
    }

    public BookResponse update(Long id, BookRequest request) {
        Book book = findBook(id);
        ensureIsbnAvailable(request.isbn(), id);
        book.updateDetails(request.title(), request.author(), request.isbn(), request.genre(),
                request.publishedYear());
        return toResponse(bookRepository.save(book));
    }

    public void delete(Long id) {
        bookRepository.delete(findBook(id));
    }

    @Transactional(readOnly = true)
    public Page<BookResponse> search(String term, Pageable pageable) {
        String normalizedTerm = term == null || term.isBlank() ? null : term.trim();
        return bookRepository.searchByTitleOrAuthor(normalizedTerm, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<BookResponse> getByGenre(String genre, Pageable pageable) {
        return bookRepository.findByGenreContainingIgnoreCase(genre.trim(), pageable).map(this::toResponse);
    }

    private Book findBook(Long id) {
        return bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
    }

    private void ensureIsbnAvailable(String isbn, Long currentBookId) {
        bookRepository.findByIsbn(isbn.trim())
                .filter(existing -> !existing.getId().equals(currentBookId))
                .ifPresent(existing -> {
                    throw new DuplicateIsbnException(isbn);
                });
    }

    private Book toEntity(BookRequest request) {
        return new Book(request.title(), request.author(), request.isbn(), request.genre(), request.publishedYear());
    }

    private BookResponse toResponse(Book book) {
        return new BookResponse(book.getId(), book.getTitle(), book.getAuthor(), book.getIsbn(), book.getGenre(),
                book.getPublishedYear());
    }
}
