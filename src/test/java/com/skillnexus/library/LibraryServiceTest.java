package com.skillnexus.library;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class LibraryServiceTest {
    static class MemoryDatabase implements LibraryRepository {
        private List<Book> saved = new ArrayList<>();
        public List<Book> loadBooks() { return new ArrayList<>(saved); }
        public void saveBooks(List<Book> books) { saved = new ArrayList<>(books); }
    }
    @Test void factoryCreatesBook() { Book book = BookFactory.createBook(1, "Dune", "Frank Herbert"); assertEquals("Dune", book.getTitle()); }
    @Test void createUpdateAndDeleteBook() {
        LibraryService service = new LibraryService(new MemoryDatabase(), new NotificationService());
        Book book = service.addBook("Dune", "Frank Herbert");
        assertEquals(1, service.getAllBooks().size());
        service.updateBook(book.getId(), "Dune Messiah", "Frank Herbert");
        assertEquals("Dune Messiah", service.findBook(book.getId()).getTitle());
        service.deleteBook(book.getId());
        assertTrue(service.getAllBooks().isEmpty());
    }
    @Test void observerReceivesEvents() {
        NotificationService notifications = new NotificationService();
        List<String> messages = new ArrayList<>();
        notifications.subscribe(messages::add);
        notifications.notifyObservers("Test event");
        assertEquals(List.of("Test event"), messages);
    }
    @Test void singletonReturnsSameInstance() { assertSame(LibraryDatabase.getInstance(), LibraryDatabase.getInstance()); }
    @Test void rejectsBlankTitle() { assertThrows(IllegalArgumentException.class, () -> BookFactory.createBook(1, " ", "Author")); }
}
