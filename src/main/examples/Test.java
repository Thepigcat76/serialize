package main.examples;

import main.internal.SerializeHelper;
import main.api.Serializer;
import main.api.annotations.Serialized;
import main.api.annotations.SerializerInstance;

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
    public void close() throws Exception {
        SerializeHelper.save(this);
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
