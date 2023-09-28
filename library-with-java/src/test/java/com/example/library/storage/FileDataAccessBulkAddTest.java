package com.example.library.storage;

import com.example.library.application.Book;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class FileDataAccessBulkAddTest {
    private static final String LIBRARY_CSV = "data/library-test.csv";
    private FileDataAccess fileDataAccess;

    @BeforeEach
    void setUp() throws IOException {
        fileDataAccess = new FileDataAccess(LIBRARY_CSV);
    }

    @AfterEach
    void tearDown() throws IOException {
        fileDataAccess.deleteAll();
    }

    @Test
    void 도서_대용량_데이터_등록_테스트() throws InterruptedException {
        int thread = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(thread);
        AtomicInteger atomic = new AtomicInteger();

        for (int i = 0; i < thread; i++) {
            executorService.execute(() -> {
                int count = atomic.incrementAndGet();
                fileDataAccess.addBook(new Book("CS" + count, "Test Book", "Author", "100"));
                latch.countDown();
            });
        }

        latch.await();

        Thread.sleep(5000);

        assertThat(fileDataAccess.countBooks()).isEqualTo(thread);
    }
}