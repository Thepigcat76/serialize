import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class Main {
    public static final Map<Class<?>, Class<? extends Serializer<?>>> DEFAULT_SERIALIZER = Map.of(
            int.class, IntSerializer.class,
            Test.SubClass.class, Test.SubClassSerializer.class
    );

    public static void main(String[] args) throws Exception {
        try (Test test = new Test()) {
            setFields(test);
        }
    }

    private static void setFields(Test test) {
        System.out.println("Old: " + test.x);
        Scanner scanner = new Scanner(System.in);
        test.x = scanner.nextInt();
        System.out.println("New: " + test.x);
        System.out.println("-----");
        System.out.println("Old: " + test.y);
        test.y = scanner.nextInt();
        System.out.println("New: " + test.y);
        System.out.println("-----");
        System.out.println("Old: " + test.z);
        test.z = scanner.nextInt();
        System.out.println("New: " + test.z);
        System.out.println("-----");
    }

    public static void save(Test instance) throws Exception {
        Field[] fields = instance.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Serialized.class)) {
                Serialized serialized = field.getAnnotation(Serialized.class);
                Class<? extends Serializer<?>> serializerClass = switch (serialized.value().length) {
                    case 0 -> {
                        Class<? extends Serializer<?>> serializer = DEFAULT_SERIALIZER.get(field.getType());
                        if (serializer != null)
                            yield serializer;
                        else
                            throw new Exception("Could not get default serializer for type: " + field.getType().getTypeName() + ". Please provide a serializer manually by passing it to the annotation");
                    }
                    case 1 -> serialized.value()[0];
                    default -> throw new Error("Provided more than one serializer for field " + field);
                };
                Method instanceMethod = getInstanceMethod(serializerClass);
                Serializer<?> serializer = null;
                try {
                    Object result = instanceMethod.invoke(null);
                    if (result instanceof Serializer<?> serializer1) {
                        serializer = serializer1;
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
                int i = field.getInt(instance);
                DataManager.INSTANCE.save(field.getName(), i);
            }
        }
    }

    public static void load(Test instance) throws IllegalAccessException {
        Map<String, Integer> loaded = DataManager.INSTANCE.load();
        System.out.println(loaded);
        Class<? extends Test> testClass = instance.getClass();
        Field[] fields = testClass.getDeclaredFields();
        for (Field field : fields) {
            for (String loadedFieldName : loaded.keySet()) {
                if (Objects.equals(loadedFieldName, field.getName())) {
                    field.setInt(instance, loaded.get(loadedFieldName));
                }
            }
        }
    }

    private static Method getInstanceMethod(Class<? extends Serializer<?>> serializerClass) throws Exception {
        Method[] methods = serializerClass.getDeclaredMethods();
        Method instanceMethod = null;
        for (Method method : methods) {
            if (method.isAnnotationPresent(SerializerInstance.class)) {
                if (!Modifier.isStatic(method.getModifiers())) {
                    throw new IllegalAccessException("Method `" + method.getName() + "` needs to be static in order to be a valid instance getter method");
                }
                if (method.getReturnType() != serializerClass) {
                    throw new Exception("Return type of method `" + method.getName()
                            + "` is not correct. Since this method is annotated as an instance getter method it needs to return the type of the instance and therefore it's class. Provided return type: "
                            + method.getReturnType().getTypeName()
                            + ", expected return type: "
                            + serializerClass.getTypeName()
                    );
                }
                if (instanceMethod == null) {
                    instanceMethod = method;
                } else {
                    throw new RuntimeException("Only one method in the Serializer class is allowed to have a @SerializerInstance annotation. Affected class: `"
                            + serializerClass.getName()
                            + "`, affected method: `"
                            + method.getName()
                            + "`, already present instance getter method: `"
                            + instanceMethod.getName()
                            + "`"
                    );
                }
            }
        }

        if (instanceMethod == null) {
            throw new RuntimeException("Class: `"
                    + serializerClass.getName()
                    + "` does not have an instance getter method. Your serializer class needs a public, static method that will return a singleton instance of your Serializer. This method also needs to be annotated with `@SerializerInstance` and have the class as its return type");
        }

        return instanceMethod;
    }
}