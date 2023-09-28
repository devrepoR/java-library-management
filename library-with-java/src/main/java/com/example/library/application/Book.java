package com.example.library.application;

import java.util.Objects;

public class Book {
    private final String isbn;
    private final String subject;
    private final String author;
    private final String totalPageCnt;

    public Book(String isbn, String subject, String author, String totalPageCnt) {
        this.isbn = isbn;
        this.subject = subject;
        this.author = author;
        this.totalPageCnt = totalPageCnt;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getSubject() {
        return subject;
    }

    public String getAuthor() {
        return author;
    }

    public String getTotalPageCnt() {
        return totalPageCnt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book book)) return false;
        return Objects.equals(isbn, book.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }
}
