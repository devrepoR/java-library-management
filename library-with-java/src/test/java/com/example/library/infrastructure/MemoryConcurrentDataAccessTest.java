package com.example.library.infrastructure;

import com.example.library.domain.Book;
import com.example.library.domain.RentedBook;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;


@SuppressWarnings("NonAsciiCharacters")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MemoryConcurrentDataAccessTest {

    private final MemoryConcurrentDataAccess memoryLockDataAccess = new MemoryConcurrentDataAccess(null);

    @Order(1)
    @Test
    void 도서_등록_테스트() throws InterruptedException {

        int SIZE = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch countDownLatch = new CountDownLatch(SIZE);

        AtomicInteger sequence = new AtomicInteger();

        for (int i = 0; i < SIZE; i++) {
            executorService.execute(() -> {
                int seq = sequence.getAndIncrement();
                Book book = new Book("CS" + seq, "토비의 스프링" + seq, "토비", 500);
                memoryLockDataAccess.addBook(new RentedBook(book));
                countDownLatch.countDown();
            });
        }

        // Wait for all threads to finish
        countDownLatch.await();

        assertThat(memoryLockDataAccess.findAllBooks().size()).isEqualTo(SIZE);
    }

    @Order(2)
    @Test
    void 도서_상태_수정_테스트() throws InterruptedException {

        int SIZE = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch countDownLatch = new CountDownLatch(SIZE);

        AtomicInteger sequence = new AtomicInteger();

        for (int i = 0; i < SIZE; i++) {
            executorService.execute(() -> {
                int seq = sequence.getAndIncrement();
                memoryLockDataAccess.updateBookStatus("CS" + seq, RentedBook.BookStatus.RENTED);
                countDownLatch.countDown();
            });
        }

        // Wait for all threads to finish
        countDownLatch.await();

        Thread.sleep(1000);

        List<RentedBook> allBooks = memoryLockDataAccess.findAllBooks().stream()
                .filter(RentedBook::isRented)
                .toList();

        assertThat(allBooks).hasSize(SIZE);
    }
}