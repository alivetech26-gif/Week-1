package com.skillnexus.taskmanager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TaskManager {
    private static final Path FILE = Path.of("tasks.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type TASK_LIST = new TypeToken<List<Task>>() {}.getType();
    private final List<Task> tasks = loadTasks();

    static class Task {
        int id;
        String title;
        String priority;
        String dueDate;
        boolean completed;
        Task(int id, String title, String priority, String dueDate) {
            this.id = id; this.title = title; this.priority = priority; this.dueDate = dueDate; this.completed = false;
        }
    }

    public static void main(String[] args) {
        new TaskManager().run();
    }

    private void run() {
        try (Scanner in = new Scanner(System.in)) {
            while (true) {
                System.out.println("\n=== Task Manager ===\n1. Create task\n2. List tasks\n3. Update task\n4. Complete task\n5. Delete task\n0. Exit");
                System.out.print("Choose: ");
                String choice = in.nextLine().trim();
                try {
                    switch (choice) {
                        case "1" -> create(in);
                        case "2" -> list();
                        case "3" -> update(in);
                        case "4" -> complete(in);
                        case "5" -> delete(in);
                        case "0" -> { saveTasks(); System.out.println("Goodbye!"); return; }
                        default -> System.out.println("Choose a valid menu option.");
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        }
    }

    private void create(Scanner in) {
        System.out.print("Task title: ");
        String title = in.nextLine().trim();
        if (title.isBlank()) throw new IllegalArgumentException("Title cannot be empty.");
        System.out.print("Priority (LOW/MEDIUM/HIGH): ");
        String priority = in.nextLine().trim().toUpperCase();
        if (!List.of("LOW", "MEDIUM", "HIGH").contains(priority)) throw new IllegalArgumentException("Priority must be LOW, MEDIUM, or HIGH.");
        System.out.print("Due date (YYYY-MM-DD): ");
        String due = in.nextLine().trim();
        try { LocalDate.parse(due); } catch (DateTimeParseException e) { throw new IllegalArgumentException("Enter a valid date as YYYY-MM-DD."); }
        int id = tasks.stream().mapToInt(t -> t.id).max().orElse(0) + 1;
        tasks.add(new Task(id, title, priority, due));
        saveTasks();
        System.out.println("Task created with ID " + id);
    }

    private void list() {
        if (tasks.isEmpty()) { System.out.println("No tasks yet."); return; }
        for (Task t : tasks) System.out.printf("%d | %s | %s | due %s | %s%n", t.id, t.title, t.priority, t.dueDate, t.completed ? "DONE" : "OPEN");
    }

    private void update(Scanner in) {
        Task t = find(readId(in));
        System.out.print("New title (leave blank to keep): ");
        String title = in.nextLine().trim();
        if (!title.isBlank()) t.title = title;
        System.out.print("New priority (LOW/MEDIUM/HIGH, blank to keep): ");
        String p = in.nextLine().trim().toUpperCase();
        if (!p.isBlank()) {
            if (!List.of("LOW", "MEDIUM", "HIGH").contains(p)) throw new IllegalArgumentException("Invalid priority.");
            t.priority = p;
        }
        System.out.print("New due date (YYYY-MM-DD, blank to keep): ");
        String d = in.nextLine().trim();
        if (!d.isBlank()) { try { LocalDate.parse(d); } catch (DateTimeParseException e) { throw new IllegalArgumentException("Invalid date."); } t.dueDate = d; }
        saveTasks(); System.out.println("Task updated.");
    }

    private void complete(Scanner in) { Task t = find(readId(in)); t.completed = true; saveTasks(); System.out.println("Task marked complete."); }
    private void delete(Scanner in) { Task t = find(readId(in)); tasks.remove(t); saveTasks(); System.out.println("Task deleted."); }
    private int readId(Scanner in) { System.out.print("Task ID: "); try { return Integer.parseInt(in.nextLine().trim()); } catch (NumberFormatException e) { throw new IllegalArgumentException("ID must be a number."); } }
    private Task find(int id) { return tasks.stream().filter(t -> t.id == id).findFirst().orElseThrow(() -> new IllegalArgumentException("No task found with that ID.")); }

    private static List<Task> loadTasks() {
        if (!Files.exists(FILE)) return new ArrayList<>();
        try { List<Task> loaded = GSON.fromJson(Files.readString(FILE), TASK_LIST); return loaded == null ? new ArrayList<>() : loaded; }
        catch (IOException | RuntimeException e) { System.out.println("Could not read tasks.json; starting with an empty task list."); return new ArrayList<>(); }
    }

    private void saveTasks() {
        try { Files.writeString(FILE, GSON.toJson(tasks)); }
        catch (IOException e) { System.out.println("Warning: unable to save tasks: " + e.getMessage()); }
    }
}
