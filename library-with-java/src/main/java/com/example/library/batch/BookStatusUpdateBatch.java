package com.example.library.batch;

import com.example.library.domain.RentedBook;
import com.example.library.infrastructure.BookDataAccess;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class BookStatusUpdateBatch {
    private static final Logger log = Logger.getLogger(BookStatusUpdateBatch.class.getName());
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final BookDataAccess dataAccess;

    public BookStatusUpdateBatch(BookDataAccess dataAccess) {
        this.dataAccess = dataAccess;
        initializeScheduler();
    }

    private void initializeScheduler() {
        log.info("[LOG] [INIT] [BATCH]");
        scheduler.scheduleAtFixedRate(this::updateBookStatuses, 0, 10, TimeUnit.SECONDS);
    }

    private void updateBookStatuses() {
        log.info("[LOG] [BATCH] [UPDATE] [START]");

        List<RentedBook> booksUnderReview = dataAccess.findAllBooks().stream()
                .filter(RentedBook::isOrganized)
                .toList();
        log.info("[LOG] [BATCH] [FIND DATA SIZE] : " + booksUnderReview.size());

        booksUnderReview.stream()
                .filter(rentedBook -> rentedBook.isOrganizingTimeOver(Duration.ofMinutes(5), LocalDateTime.now()))
                .forEach(rentedBook -> {
                    log.info("Book Updated ORGANIZING to AVAILABLE: " + rentedBook.getIsbn());
                    dataAccess.updateBookStatus(rentedBook.getIsbn(), RentedBook.BookStatus.AVAILABLE);
                });
        log.info("[LOG] [BATCH] [UPDATE] [END]");
    }

    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }
}
