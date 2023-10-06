package com.example.library.domain;

import com.example.library.utils.ConvertUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

import static com.example.library.utils.FileConstant.FILE_DELIMITER;
import static java.time.LocalDateTime.now;

public class RentedBook {
    private final Book book;
    private LocalDateTime rentedAt;
    private BookStatus status;
    private LocalDateTime returnedAt;

    public RentedBook(Book book) {
        this(book, now(), BookStatus.AVAILABLE);
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

    public boolean isAvailable() {
        return this.status == BookStatus.AVAILABLE;
    }

    public boolean isRented() {
        return this.status == BookStatus.RENTED;
    }

    public BookStatus getStatus() {
        return status;
    }

    public boolean isOrganized() {
        return this.status == BookStatus.ORGANIZING;
    }

    public void rent() {
        if(isOrganized()) {
            throw new RuntimeException("정리중인 도서는 반납할 수 없습니다.");
        } else if(isRented()) {
            throw new RuntimeException("대여중인 도서는 반납할 수 없습니다.");
        }

        updateStatus(BookStatus.RENTED);
        this.rentedAt = now();
    }

    public void available() {
        if (isReturned()) {
            throw new RuntimeException("반납된 도서는 대여 가능으로 변경할 수 없습니다.");
        } else if(isLost()) {
            throw new RuntimeException("분실된 도서는 대여 가능으로 변경할 수 없습니다.");
        } else if(isRented()) {
            throw new RuntimeException("대여중인 도서는 대여 가능으로 변경할 수 없습니다.");
        }
        updateStatus(BookStatus.AVAILABLE);
    }

    public void organize() { // return 시 정리중으로 변경
        if (isOrganized()) {
            throw new RuntimeException("이미 정리중인 도서입니다.");
        } else if (isRented()) {
            throw new RuntimeException("대여중인 도서는 정리중으로 변경할 수 없습니다.");
        }

        updateStatus(BookStatus.ORGANIZING);
    }

    public void lost() {
        if (isAvailable()) {
            throw new RuntimeException("대여 가능한 도서는 분실됨 상태로 변경할 수 없습니다.");
        } else if (isOrganized()) {
            throw new RuntimeException("정리중인 도서는 분실됨 상태로 변경할 수 없습니다.");
        }

        updateStatus(BookStatus.LOST);
    }

    public void returnBook() {
        if (isAvailable()) {
            throw new RuntimeException("대여 가능한 도서는 반납할 수 없습니다.");
        } else if (isOrganized()) {
            throw new RuntimeException("정리중인 도서는 반납할 수 없습니다.");
        } else if (isReturned()) {
            throw new RuntimeException("이미 반납된 도서입니다.");
        }

        updateStatus(BookStatus.AVAILABLE);
        this.returnedAt = now();
    }

    public void updateStatus(BookStatus status) {
        this.status = status;
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
        return String.join(FILE_DELIMITER
                , book.getIsbn()
                , book.getSubject()
                , book.getAuthor()
                , String.valueOf(book.getTotalPageCnt())
                , Optional.of(ConvertUtils.convertLocalDateTimeToString(this.rentedAt)).orElse(null)
                , ConvertUtils.convertLocalDateTimeToString(this.returnedAt)
                , status.name());
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

        public static BookStatus of(String bookStatus) {
            return Arrays.stream(values())
                    .filter(status -> status.name().equals(bookStatus))
                    .findAny()
                    .orElse(LOST);
        }
    }
}
