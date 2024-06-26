package main;

import main.api.Serializer;
import main.examples.AnotherTest;
import main.examples.Test;
import main.internal.IntSerializer;
import main.internal.StringSerializer;

import java.util.*;

public class Main {
    public static final Map<Class<?>, Class<? extends Serializer<?>>> DEFAULT_SERIALIZER = Map.of(
            int.class, IntSerializer.class,
            String.class, StringSerializer.class
    );

    public static void main(String[] args) throws Exception {
        /*
        try (Test test = new Test()) {
            setFields(test);
        }
         */

        try (AnotherTest test = new AnotherTest()) {
            System.out.println("Old: "+test.someString);
            Scanner scanner = new Scanner(System.in);
            test.someString = scanner.nextLine();
            System.out.println("New: "+test.someString);
        }
    }

    private static void setFields(Test test) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Old: " + test.y);
        test.y = scanner.nextInt();
        System.out.println("New: " + test.y);
        System.out.println("-----");
        System.out.println("Old: " + test.z);
        test.z = scanner.nextInt();
        System.out.println("New: " + test.z);
        System.out.println("-----");
    }

}