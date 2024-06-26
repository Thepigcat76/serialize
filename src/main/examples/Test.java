package main.examples;

import main.internal.SerializeHelper;
import main.api.annotations.Serialized;

public class Test implements AutoCloseable {
    @Serialized
    public static int x;
    @Serialized
    public int y;
    @Serialized
    public int z;

    public Test() {
        SerializeHelper.load(this);
    }

    @Override
    public void close() {
        SerializeHelper.save(this);
    }
}
