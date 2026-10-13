package com.skillnexus.calculator;

import java.util.Scanner;

interface Operation {
    double apply(double a, double b);
}

class Addition implements Operation {
    public double apply(double a, double b) { return a + b; }
}

class Subtraction implements Operation {
    public double apply(double a, double b) { return a - b; }
}

class Multiplication implements Operation {
    public double apply(double a, double b) { return a * b; }
}

class Division implements Operation {
    public double apply(double a, double b) {
        if (b == 0) throw new ArithmeticException("Cannot divide by zero.");
        return a / b;
    }
}

public class CalculatorApp {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter first number: ");
            double a = Double.parseDouble(scanner.nextLine().trim());
            System.out.print("Enter operator (+, -, *, /): ");
            String symbol = scanner.nextLine().trim();
            System.out.print("Enter second number: ");
            double b = Double.parseDouble(scanner.nextLine().trim());
            Operation operation = switch (symbol) {
                case "+" -> new Addition();
                case "-" -> new Subtraction();
                case "*" -> new Multiplication();
                case "/" -> new Division();
                default -> throw new IllegalArgumentException("Unsupported operator.");
            };
            System.out.println("Result: " + operation.apply(a, b));
        } catch (NumberFormatException e) {
            System.out.println("Please enter valid numbers.");
        } catch (IllegalArgumentException | ArithmeticException e) {
            System.out.println(e.getMessage());
        }
    }
}
