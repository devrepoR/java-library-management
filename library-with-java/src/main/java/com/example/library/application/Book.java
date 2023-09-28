package com.example.library.application;

import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Logger;

public class Book {
    private final String isbn;
    private final String subject;
    private final String author;
    private final String totalPageCnt;
    private BookStatus status;

    public Book(String isbn, String subject, String author, String totalPageCnt) {
        this(isbn, subject, author, totalPageCnt, BookStatus.AVAILABLE);
    }

    public Book(String isbn, String subject, String author, String totalPageCnt, BookStatus bookStatus) {
        this.isbn = isbn;
        this.subject = subject;
        this.author = author;
        this.totalPageCnt = totalPageCnt;
        this.status = bookStatus;
    }

    public void changeStatus(BookStatus status) {
        this.status = status;
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

    public BookStatus getStatus() {
        return status;
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

    @Override
    public String toString() {
        return "Book{" +
                "isbn='" + isbn + '\'' +
                ", subject='" + subject + '\'' +
                ", author='" + author + '\'' +
                ", totalPageCnt=" + totalPageCnt +
                ", status=" + status +
                '}';
    }

    public enum BookStatus {
        AVAILABLE("대여 가능"),
        RENTED("대여중"),
        ORGANIZING("도서 정리중"),
        LOST("분실됨");
        private static final Logger log = Logger.getLogger(BookStatus.class.getName());
        private final String title;

        BookStatus(String title) {
            this.title = title;
        }

        // 뭐.. description 은 아직 사용 x
        public String getTitle() {
            return title;
        }

        public static BookStatus of(String bookStatus) {
            return Arrays.stream(values())
                    .filter(status -> status.name().equals(bookStatus))
                    .findAny()
                    .orElse(LOST);
        }
    }
}
