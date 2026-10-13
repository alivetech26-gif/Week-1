package com.skillnexus.library;

import java.util.List;

abstract class LibraryDatabaseForTest extends LibraryDatabaseAdapter {
    @Override public abstract List<Book> loadBooks();
    @Override public abstract void saveBooks(List<Book> books);
}
