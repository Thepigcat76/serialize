package main.internal;

import main.api.Serializer;
import main.api.annotations.AutoRegisterSerializer;
import main.api.annotations.SerializerInstance;
import main.examples.Test;

@AutoRegisterSerializer
public class IntSerializer extends Serializer<Integer> {
    private static final IntSerializer INSTANCE = new IntSerializer(new Test.SubClassSerializer[]{Test.SubClassSerializer.INSTANCE});

    public IntSerializer(Serializer<?>[] subSerializer) {
        super(subSerializer);
    }

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
