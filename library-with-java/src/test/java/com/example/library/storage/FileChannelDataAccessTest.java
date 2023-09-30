package com.example.library.storage;

import com.example.library.application.Book;
import com.example.library.application.RentedBook;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SuppressWarnings("NonAsciiCharacters")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class FileChannelDataAccessTest {


    private static final String LIBRARY_CSV = "data/library-test.csv";
    private static final int THREAD_COUNT = 500;
    private FileChannelDataAccess fileDataAccess;
    @BeforeEach
    void setUp() throws IOException {
        fileDataAccess = new FileChannelDataAccess(LIBRARY_CSV);
    }

    @Order(1)
    @Test
    void 도서_등록_테스트() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        AtomicInteger atomic = new AtomicInteger();

        // 먼저 책 등록
        for (int i = 0; i < THREAD_COUNT; i++) {
            executorService.execute(() -> {
                int count = atomic.incrementAndGet();
                Book book = new Book("CS" + count, "Test Book", "Author", "100");
                fileDataAccess.addBook(new RentedBook(book));
                latch.countDown();
            });
        }

        latch.await();

        assertThat(fileDataAccess.countBooks()).isEqualTo(THREAD_COUNT); // 책이 모두 등록되었는지 확인
    }

    @Order(2)
    @Test
    void 도서_상태_수정_테스트() throws InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        AtomicInteger atomic = new AtomicInteger();

        for (int i = 0; i < THREAD_COUNT; i++) {
            executorService.execute(() -> {
                int count = atomic.incrementAndGet();
                fileDataAccess.updateBookStatus("CS" + count, RentedBook.BookStatus.RENTED);
                latch.countDown();
            });
        }

        latch.await();

        assertThat(fileDataAccess.countBooks()).isEqualTo(THREAD_COUNT); // 책이 모두 등록되었는지 확인

        long rentedBooksCount = fileDataAccess.findAllBooks()
                .stream()
                .filter(book -> book.getStatus() == RentedBook.BookStatus.RENTED)
                .count();

        assertThat(rentedBooksCount).isEqualTo(THREAD_COUNT); // 모든 책이 RENTED 상태로 수정되었는지 확인
    }

    @Order(3)
    @Test
    void 전체_삭제() throws IOException {
        fileDataAccess.deleteAll();
    }

    @Test
    void 전체_조회() {
        List<RentedBook> allBooks = fileDataAccess.findAllBooks().stream()
                .filter(book -> book.getStatus() == RentedBook.BookStatus.ORGANIZING)
                .toList();
        assertThat(allBooks.size()).isEqualTo(500);
    }
}