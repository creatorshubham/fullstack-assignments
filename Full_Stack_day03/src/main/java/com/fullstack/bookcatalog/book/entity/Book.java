package com.fullstack.bookcatalog.book.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 160)
    private String title;

    @Column(nullable = false, length = 120)
    private String author;

    @Column(nullable = false, unique = true, length = 13)
    private String isbn;

    @Column(nullable = false, length = 60)
    private String genre;

    @Column(name = "published_year", nullable = false)
    private int publishedYear;

    protected Book() {
    }

    public Book(String title, String author, String isbn, String genre, int publishedYear) {
        updateDetails(title, author, isbn, genre, publishedYear);
    }

    public void updateDetails(String title, String author, String isbn, String genre, int publishedYear) {
        this.title = title.trim();
        this.author = author.trim();
        this.isbn = isbn.trim();
        this.genre = genre.trim();
        this.publishedYear = publishedYear;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getGenre() {
        return genre;
    }

    public int getPublishedYear() {
        return publishedYear;
    }
}
