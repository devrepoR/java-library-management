package com.example.library.storage;

import com.example.library.application.Book;
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
        Book book = new Book("CS001", "Test Book", "Author", "100");
        fileDataAccess.addBook(book);

        Optional<Book> retrievedBook = fileDataAccess.findBookByIsbn("CS001");
        assertThat(retrievedBook).isPresent();
    }

    @Test
    void 도서_상태_변경_테스트() {
        Book book = new Book("CS002", "Test Book", "Author", "100");
        fileDataAccess.addBook(book);

        Book.BookStatus newStatus = Book.BookStatus.RENTED;
        assertThat(fileDataAccess.updateBookStatus("CS002", newStatus))
                .isEqualTo(true);

        Optional<Book> retrievedBook = fileDataAccess.findBookByIsbn("CS002");

        assertThat(retrievedBook)
                .isPresent()
                .hasValueSatisfying(b -> assertThat(b.getStatus()).isEqualTo(newStatus));

    }

    @Test
    void 도서_상태_변경_도서정리중_테스트() {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(LIBRARY_CSV))) {
            writer.write("CS001,Test Book,Author,100,RENTED");
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertThat(fileDataAccess.updateBookStatus("CS001", Book.BookStatus.ORGANIZING))
                .isEqualTo(true);

        fileDataAccess.findBookByIsbn("CS001")
                .ifPresent(b -> assertThat(b.getStatus()).isEqualTo(Book.BookStatus.ORGANIZING));
    }

    @Test
    void 도서_삭제_테스트() {
        Book book = new Book("CS003", "Test Book", "Author", "100");
        fileDataAccess.addBook(book);

        assertThat(fileDataAccess.removeBookByIsbn("CS003")).isTrue();

        assertThat(fileDataAccess.findBookByIsbn("CS003"))
                .isEmpty();
    }
}