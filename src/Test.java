public class Test implements AutoCloseable {
    @Serialized
    public int x;
    @Serialized
    public int y;
    @Serialized
    public int z;

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

    public static class SubClassSerializer extends Serializer<SubClass> {
        public static final SubClassSerializer INSTANCE = new SubClassSerializer(new Serializer[]{});

        public SubClassSerializer(Serializer<?>[] subSerializer) {
            super(subSerializer);
        }

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
