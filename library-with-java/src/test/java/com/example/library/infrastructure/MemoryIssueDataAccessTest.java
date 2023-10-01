package com.example.library.infrastructure;

import com.example.library.domain.Book;
import com.example.library.domain.RentedBook;
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
class MemoryIssueDataAccessTest {

    private MemoryIssueDataAccess memoryIssueDataAccess;

    @BeforeEach
    void setUp() {
        this.memoryIssueDataAccess = new MemoryIssueDataAccess();
    }

    /**
     * 문제가 발생하는 테스트
     */
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
                memoryIssueDataAccess.addBook(new RentedBook(book));
                countDownLatch.countDown();
            });
        }

        // Wait for all threads to finish
        countDownLatch.await();

        assertThat(memoryIssueDataAccess.countBooks()).isNotEqualTo(SIZE);

    }
}