package com.example.library.application;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Logger;

import static java.time.LocalDateTime.now;

public class RentedBook {
    private final Book book;
    private final LocalDateTime rentedAt;
    private BookStatus status;
    private LocalDateTime returnedAt;

    public RentedBook(Book book) {
        this(book, now(), BookStatus.ORGANIZING);
    }

    public RentedBook(Book book, BookStatus status) {
        this(book, now(), status);
    }

    public RentedBook(Book book, LocalDateTime rentedAt, BookStatus status) {
        this(book, rentedAt, null, status);
    }

    public RentedBook(Book book, LocalDateTime rentedAt, LocalDateTime returnedAt, BookStatus status) {
        this.book = book;
        this.rentedAt = rentedAt;
        this.returnedAt = returnedAt;
        this.status = status;
    }

    public void returnBook() {
        this.status = BookStatus.RENTED;
        this.returnedAt = now();
    }

    public boolean isReturned() {
        return returnedAt != null;
    }

    public boolean isOrganizingTimeOver(Duration organizingDuration, LocalDateTime timeAt) {
        if (!isOrganized()) return false;
        Duration durationSinceReturn = Duration.between(returnedAt, timeAt);
        return durationSinceReturn.compareTo(organizingDuration) >= 0;
    }

    public String getIsbn() {
        return book.getIsbn();
    }

    public Book getBook() {
        return book;
    }

    public LocalDateTime getRentedTime() {
        return rentedAt;
    }

    public LocalDateTime getReturnedTime() {
        return returnedAt;
    }

    public void available() {
        this.status = BookStatus.AVAILABLE;
    }

    public boolean isAvailable() {
        return this.status == BookStatus.AVAILABLE;
    }

    public boolean isRented() {
        return this.status == BookStatus.RENTED;
    }

    public void organize() {
        this.status = BookStatus.ORGANIZING;
    }

    public void updateStatus(BookStatus status) {
        this.status = status;
    }

    public BookStatus getStatus() {
        return status;
    }

    public boolean isOrganized() {
        return this.status == BookStatus.ORGANIZING;
    }

    public void lost() {
        this.status = BookStatus.LOST;
    }

    public boolean isLost() {
        return this.status == BookStatus.LOST;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RentedBook that)) return false;
        return Objects.equals(book, that.book);
    }

    @Override
    public int hashCode() {
        return Objects.hash(book);
    }

    @Override
    public String toString() {
        String rented = this.rentedAt != null ? this.rentedAt.toString() : "";
        String returned = this.returnedAt != null ? this.returnedAt.toString() : "";

        return String.join(",", book.getIsbn(), book.getSubject(), book.getAuthor(),
                book.getTotalPageCnt(),
                rented,
                returned,
                status.name());
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
