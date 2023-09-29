package com.example.library.batch;

import com.example.library.application.Book;
import com.example.library.application.RentedBook;
import com.example.library.storage.FileChannelDataAccess;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SuppressWarnings("NonAsciiCharacters")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BookStatusUpdaterTest {
    private static final String LIBRARY_CSV = "data/library-test.csv";
    private static final int THREAD_COUNT = 500;
    private FileChannelDataAccess fileDataAccess;

    @BeforeEach
    void setUp() {
        fileDataAccess = new FileChannelDataAccess(LIBRARY_CSV);
        new BookStatusUpdater(fileDataAccess);
    }

    @Order(1)
    @Test
    void 데이터_추가_테스트() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        AtomicInteger atomic = new AtomicInteger();

        // 먼저 책 등록
        for (int i = 0; i < THREAD_COUNT; i++) {
            executorService.execute(() -> {
                int count = atomic.incrementAndGet();
                Book book = new Book("CS" + count, "Test Book", "Author", "100");
                fileDataAccess.addBook(new RentedBook(book, LocalDateTime.now().minusMinutes(10), LocalDateTime.now().plusMinutes(5), RentedBook.BookStatus.ORGANIZING));
                latch.countDown();
            });
        }

        latch.await();

        assertThat(fileDataAccess.countBooks()).isEqualTo(THREAD_COUNT); // 책이 모두 등록되었는지 확인
    }
}