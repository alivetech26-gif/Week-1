package com.skillnexus.library;

import java.util.Scanner;

public class LibraryApp {
    public static void main(String[] args) {
        LibraryService library = new LibraryService();
        library.notifications().subscribe(message -> System.out.println("[Notification] " + message));
        try (Scanner input = new Scanner(System.in)) {
            while (true) {
                System.out.println("\n=== Library Management ===\n1. Add book\n2. List books\n3. Update book\n4. Delete book\n5. Borrow book\n6. Return book\n0. Exit");
                System.out.print("Choose: ");
                String choice = input.nextLine().trim();
                try {
                    switch (choice) {
                        case "1" -> { System.out.print("Title: "); String title = input.nextLine(); System.out.print("Author: "); String author = input.nextLine(); System.out.println("Added: " + library.addBook(title, author)); }
                        case "2" -> { if (library.getAllBooks().isEmpty()) System.out.println("No books found."); else library.getAllBooks().forEach(System.out::println); }
                        case "3" -> { int id = readId(input); System.out.print("New title: "); String title = input.nextLine(); System.out.print("New author: "); String author = input.nextLine(); library.updateBook(id, title, author); }
                        case "4" -> library.deleteBook(readId(input));
                        case "5" -> library.setAvailability(readId(input), false);
                        case "6" -> library.setAvailability(readId(input), true);
                        case "0" -> { System.out.println("Goodbye!"); return; }
                        default -> System.out.println("Choose a valid option.");
                    }
                } catch (IllegalArgumentException | IllegalStateException e) { System.out.println("Error: " + e.getMessage()); }
            }
        }
    }
    private static int readId(Scanner input) { System.out.print("Book ID: "); try { return Integer.parseInt(input.nextLine().trim()); } catch (NumberFormatException e) { throw new IllegalArgumentException("ID must be a number."); } }
}