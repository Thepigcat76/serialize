public class Test {
    @Serialized
    public int x;

    @Serialized(SubClassSerializer.class)
    public SubClass y;

    public Test() {
        this.x = 69;
        this.y = new SubClass();
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
