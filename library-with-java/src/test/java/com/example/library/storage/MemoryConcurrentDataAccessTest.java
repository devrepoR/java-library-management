package com.example.library.storage;

import com.example.library.application.Book;
import com.example.library.application.RentedBook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MemoryConcurrentDataAccessTest {

    private MemoryConcurrentDataAccess memoryLockDataAccess;

    @BeforeEach
    void setUp() {
        memoryLockDataAccess = new MemoryConcurrentDataAccess(null);
    }

    @Test
    void 도서_등록_테스트() throws InterruptedException {

        int SIZE = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch countDownLatch = new CountDownLatch(SIZE);

        AtomicInteger sequence = new AtomicInteger();

        for (int i = 0; i < SIZE; i++) {
            executorService.execute(() -> {
                int seq = sequence.getAndIncrement();
                Book book = new Book("CS" + seq, "토비의 스프링" + seq, "토비", "500");
                memoryLockDataAccess.addBook(new RentedBook(book));
                countDownLatch.countDown();
            });
        }

        // Wait for all threads to finish
        countDownLatch.await();

        assertThat(memoryLockDataAccess.findAllBooks().size()).isEqualTo(SIZE);
    }
}