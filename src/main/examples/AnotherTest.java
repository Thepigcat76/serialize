package main.examples;

import main.api.annotations.Serialized;
import main.internal.SerializeHelper;

public class AnotherTest implements AutoCloseable {
    @Serialized
    public String someString;

    public AnotherTest() {
        SerializeHelper.load(this);
    }

    @Override
    public void close() {
        SerializeHelper.save(this);
    }
}
