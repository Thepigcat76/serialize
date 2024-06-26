package main.internal;

import main.api.Serializer;
import main.api.annotations.AutoRegisterSerializer;
import main.api.annotations.SerializerInstance;
import main.examples.Test;

@AutoRegisterSerializer
public class IntSerializer extends Serializer<Integer> {
    private static final IntSerializer INSTANCE = new IntSerializer();

    @Override
    public int serialize(Integer integer) {
        return integer;
    }

    @Override
    public Integer load(int data) {
        return data;
    }

    @SerializerInstance
    public static IntSerializer getInstance() {
        return INSTANCE;
    }
}
