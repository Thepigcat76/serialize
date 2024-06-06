public interface Serializer<T> {
    int serialize();
    T load();
}
