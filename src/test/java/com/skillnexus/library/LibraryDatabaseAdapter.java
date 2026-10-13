package com.skillnexus.library;

import java.util.List;

abstract class LibraryDatabaseAdapter {
    public abstract List<Book> loadBooks();
    public abstract void saveBooks(List<Book> books);
}
