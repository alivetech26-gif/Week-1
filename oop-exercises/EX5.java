abstract class Vehicle {
    abstract void start();
}

class Car extends Vehicle {
    void start() {
        System.out.println("Car starts with a key");
    }
}

public class EX5 {
    public static void main(String[] args) {
        Vehicle vehicle = new Car();
        vehicle.start();
    }
}