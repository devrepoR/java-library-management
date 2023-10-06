package com.example.library.infrastructure;

import com.example.library.domain.Book;
import com.example.library.domain.RentedBook;
import org.junit.jupiter.api.*;

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


    private static final String LIBRARY_CSV = "data/library-file-channel-test.csv";
    private static final int THREAD_COUNT = 500;
    private FileChannelDataAccess fileDataAccess;

    @BeforeEach
    void setUp() {
        fileDataAccess = new FileChannelDataAccess(LIBRARY_CSV);
    }

    @Order(1)
    @Test
    void 도서_등록_테스트() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        AtomicInteger atomic = new AtomicInteger(0);

        // 먼저 책 등록
        for (int i = 0; i < THREAD_COUNT; i++) {
            executorService.execute(() -> {
                int count = atomic.incrementAndGet();

                RentedBook rentedBook = new RentedBook(new Book("CS" + count, "Test Book", "Author", 100));
                fileDataAccess.addBook(rentedBook);

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
        AtomicInteger atomic = new AtomicInteger(0);

        for (int i = 0; i < THREAD_COUNT; i++) {
            executorService.execute(() -> {
                int count = atomic.incrementAndGet();

                RentedBook rentedBook = fileDataAccess.findBookByIsbn("CS" + count).get();
                rentedBook.checkout();

                fileDataAccess.changeBook(rentedBook);

                latch.countDown();
            });
        }

        latch.await();

        long rentedBooksCount = fileDataAccess.findAllBooks()
                .stream()
                .filter(RentedBook::isRented)
                .count();

        assertThat(rentedBooksCount).isEqualTo(THREAD_COUNT); // 모든 책이 RENTED 상태로 수정되었는지 확인
    }

    @Order(3)
    @Test
    void 전체_삭제() {
        fileDataAccess.deleteAll();
    }
}