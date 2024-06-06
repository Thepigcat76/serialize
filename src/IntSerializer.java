public class IntSerializer implements Serializer<Integer> {
    private static final IntSerializer INSTANCE = new IntSerializer();

    @Override
    public int serialize() {
        return 69;
    }

    @Override
    public Integer load() {
        return 69;
    }

    @SerializerInstance
    public static IntSerializer getInstance() {
        return INSTANCE;
    }
}
