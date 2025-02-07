package com.example.library.infrastructure;

import com.example.library.domain.Book;
import com.example.library.domain.RentedBook;
import org.junit.jupiter.api.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class FileDataAccessTest {

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
    void 도서_추가_테스트() {
        Book book = new Book("CS001", "Test Book", "Author", 100);
        fileDataAccess.addBook(new RentedBook(book));

        Optional<RentedBook> retrievedBook = fileDataAccess.findBookByIsbn("CS001");
        assertThat(retrievedBook).isPresent();
    }

    @Test
    void 도서_상태_변경_테스트() {
        Book book = new Book("CS002", "Test Book", "Author", 100);
        RentedBook rentedBook = new RentedBook(book);
        fileDataAccess.addBook(rentedBook);

        rentedBook.checkout();
        assertThat(fileDataAccess.changeBook(rentedBook))
                .isEqualTo(true);

        Optional<RentedBook> retrievedBook = fileDataAccess.findBookByIsbn("CS002");

        assertThat(retrievedBook)
                .isPresent()
                .hasValueSatisfying(b -> assertThat(b.getStatus()).isEqualTo(rentedBook.getStatus()));

    }

    @Test
    void 도서_상태_변경_도서정리중_테스트() {

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(LIBRARY_CSV))) {
            writer.write("CS001,Test Book,Author,100,2023-09-29 00:00:00,2023-09-29 23:59:59,RENTED");
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

//        Book book = new Book("CS001", "Test Book", "Author", 100);
//        RentedBook rentedBook = new RentedBook(book, RentedBook.BookStatus.ORGANIZING);
//        assertThat(fileDataAccess.changeBook(rentedBook))
//                .isEqualTo(true);
//
//        fileDataAccess.findBookByIsbn("CS001")
//                .ifPresent(b -> assertThat(b.getStatus()).isEqualTo(RentedBook.BookStatus.ORGANIZING));
    }

    @Test
    void 도서_삭제_테스트() {
        Book book = new Book("CS003", "Test Book", "Author", 100);
        fileDataAccess.addBook(new RentedBook(book));

        assertThat(fileDataAccess.removeBookByIsbn("CS003")).isTrue();

        assertThat(fileDataAccess.findBookByIsbn("CS003"))
                .isEmpty();
    }
}