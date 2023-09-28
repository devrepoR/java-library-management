package com.example.library.storage;

import com.example.library.application.Book;
import com.example.library.application.RentedBook;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * 검증 불가
 */

@SuppressWarnings("NonAsciiCharacters")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class FileDataAccessBulkUpdateTest {

    private static final String LIBRARY_CSV = "data/library-test.csv";
    private FileDataAccess fileDataAccess;

    @BeforeEach
    void setUp() throws IOException {
        fileDataAccess = new FileDataAccess(LIBRARY_CSV);
    }

    @Order(1)
    @Test
    void 도서_등록_테스트() throws InterruptedException {
        int threadCount = 500;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger atomic = new AtomicInteger();

        // 먼저 책 등록
        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                int count = atomic.incrementAndGet();
                Book book = new Book("CS" + count, "Test Book", "Author", "100");
                fileDataAccess.addBook(new RentedBook(book));
                latch.countDown();
            });
        }

        latch.await();

        assertThat(fileDataAccess.countBooks()).isEqualTo(threadCount); // 책이 모두 등록되었는지 확인
    }

    @Order(2)
    @Test
    void 도서_상태_수정_테스트() throws InterruptedException {
        int threadCount = 500;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger atomic = new AtomicInteger();

        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                int count = atomic.incrementAndGet();
                fileDataAccess.updateBookStatus("CS" + count, RentedBook.BookStatus.RENTED);
                latch.countDown();
            });
        }

        latch.await();

        long rentedBooksCount = fileDataAccess.findAllBooks()
                .stream()
                .filter(book -> book.getStatus() == RentedBook.BookStatus.RENTED)
                .count();

        assertThat(rentedBooksCount).isEqualTo(threadCount); // 모든 책이 RENTED 상태로 수정되었는지 확인
    }

    @Order(3)
    @Test
    void 전체_삭제() throws IOException {
        fileDataAccess.deleteAll();
    }
}