package main.internal;

import main.api.Serializer;
import main.api.annotations.SerializerInstance;

public class StringSerializer extends Serializer<String> {
    private static final StringSerializer INSTANCE = new StringSerializer();

    @Override
    public int serialize(String data) {
        byte[] chars = data.getBytes();
        int retVal = 0;
        for (int i = 0; i < 4; i++) {
            retVal |= ((int) chars[i]) << (i * 8);
        }
        return retVal;
    }

    @Override
    public String load(int data) {
        byte[] retVal = new byte[4];
        for (int i = 0; i < 4; i++) {
            retVal[i] = (byte) ((data >> (i * 8)) & 0xFF);
        }
        return new String(retVal);
    }

    @SerializerInstance
    public static StringSerializer getInstance() {
        return INSTANCE;
    }
}
