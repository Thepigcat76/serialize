import java.lang.reflect.Field;
import java.util.Map;

public class Main {
    public static final Map<Class<?>, Class<? extends Serializer<?>>> DEFAULT_SERIALIZER = Map.of(
            int.class, IntSerializer.class
    );

    public static final Map<Class<? extends Serializer<?>>, ? extends Serializer<?>> SERIALIZER = Map.of(
            IntSerializer.class, IntSerializer.INSTANCE
    );

    public static void main(String[] args) throws Exception {
        Test test = new Test();

        Field[] fields = Test.class.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Serialized.class)) {
                Serialized serialized = field.getAnnotation(Serialized.class);
                Class<? extends Serializer<?>> serializerClass = switch (serialized.value().length) {
                    case 0 -> {
                        Class<? extends Serializer<?>> serializer = DEFAULT_SERIALIZER.get(field.getType());
                        if (serializer != null)
                            yield serializer;
                        else throw new Exception("Could not get default serializer for type: "+field.getType().getTypeName()+". Please provide a serializer manually by passing it to the annotation");
                    }
                    case 1 -> serialized.value()[0];
                    default -> throw new Error("Provided more than one serializer for field "+field);
                };
                Serializer<?> serializer = SERIALIZER.get(serializerClass);
                System.out.println("serializer: "+serializer);
                System.out.println("Field: " + field.getName() + " is serialized");
                int i = (int) field.get(test);
                System.out.println(i);
            }
        }
    }
}