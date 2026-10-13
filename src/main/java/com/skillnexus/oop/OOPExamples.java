package com.skillnexus.oop;

class Person {
    private final String name;
    Person(String name) { this.name = name; }
    public String getName() { return name; }
}

class Student extends Person {
    Student(String name) { super(name); }
    public String role() { return "Student"; }
}

class Teacher extends Person {
    Teacher(String name) { super(name); }
    public String role() { return "Teacher"; }
}

interface Shape {
    double area();
}

class Circle implements Shape {
    private final double radius;
    Circle(double radius) { this.radius = Math.max(0, radius); }
    public double area() { return Math.PI * radius * radius; }
}

class Rectangle implements Shape {
    private final double width;
    private final double height;
    Rectangle(double width, double height) { this.width = Math.max(0, width); this.height = Math.max(0, height); }
    public double area() { return width * height; }
}

public class OOPExamples {
    public static void main(String[] args) {
        Person person = new Person("Asha");
        System.out.println("Class and object: " + person.getName());
        Student student = new Student("Ravi");
        System.out.println("Inheritance: " + student.getName() + " is a " + student.role());
        Person teacher = new Teacher("Meera");
        System.out.println("Polymorphism: " + ((Teacher) teacher).role());
        System.out.println("Encapsulation: name is accessed through getName(): " + person.getName());
        Shape[] shapes = {new Circle(2), new Rectangle(3, 4)};
        for (Shape shape : shapes) System.out.println("Abstraction / polymorphism area: " + shape.area());
    }
}
