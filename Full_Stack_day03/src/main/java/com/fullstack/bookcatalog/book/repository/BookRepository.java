package com.fullstack.bookcatalog.book.repository;

import com.fullstack.bookcatalog.book.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);

    Page<Book> findByGenreContainingIgnoreCase(String genre, Pageable pageable);

    @Query("""
            select b from Book b
            where (:term is null
                or lower(b.title) like lower(concat('%', :term, '%'))
                or lower(b.author) like lower(concat('%', :term, '%')))
            """)
    Page<Book> searchByTitleOrAuthor(@Param("term") String term, Pageable pageable);
}
