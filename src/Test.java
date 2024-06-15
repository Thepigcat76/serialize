public class Test implements AutoCloseable {
    @Serialized
    public int x;
    public SubClass y;

    public Test() {
        try {
            Main.load(this);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {
        Main.save(this);
    }

    public static class SubClass {
        @Serialized
        private int a;
    }

    private static class SubClassSerializer implements Serializer<SubClass> {
        public static final SubClassSerializer INSTANCE = new SubClassSerializer();

        @Override
        public int serialize() {
            return 0;
        }

        @Override
        public SubClass load() {
            return null;
        }

        @SerializerInstance
        public static SubClassSerializer getInstance() {
            return INSTANCE;
        }
    }
}
