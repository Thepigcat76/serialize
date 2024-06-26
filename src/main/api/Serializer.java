package main.api;

public abstract class Serializer<T> {
    private final Serializer<?>[] subSerializer;

    public Serializer(Serializer<?>[] subSerializer) {
        this.subSerializer = subSerializer;
    }

    public abstract int serialize();

    public abstract T load();
}
