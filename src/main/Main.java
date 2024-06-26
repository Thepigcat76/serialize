package main;

import main.api.Serializer;
import main.examples.Test;
import main.internal.IntSerializer;

import java.util.*;

public class Main {
    public static final Map<Class<?>, Class<? extends Serializer<?>>> DEFAULT_SERIALIZER = Map.of(
            int.class, IntSerializer.class,
            Test.SubClass.class, Test.SubClassSerializer.class
    );

    public static void main(String[] args) throws Exception {
        try (Test test = new Test()) {
            setFields(test);
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