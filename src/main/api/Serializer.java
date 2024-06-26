package main.api;

public abstract class Serializer<T> {
    public abstract int serialize(T data);

    public abstract T load(int data);
}
