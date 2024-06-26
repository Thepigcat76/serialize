package main.internal;

import main.Main;
import main.api.Serializer;
import main.api.annotations.Serialized;
import main.api.annotations.SerializerInstance;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Objects;

public class SerializeHelper {
    public static void load(Object instance) {
        try {
            loadRaw(instance);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void loadRaw(Object instance) throws Exception {
        Map<String, Integer> loaded = DataManager.INSTANCE.load();
        System.out.println(loaded);
        Class<?> testClass = instance.getClass();
        Field[] fields = testClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Serialized.class)) {
                for (String loadedFieldName : loaded.keySet()) {
                    if (Objects.equals(loadedFieldName, field.getName())) {
                        if (isNotFinal(field)) {
                            Serialized serialized = field.getAnnotation(Serialized.class);
                            Class<? extends Serializer<?>> serializerClass = getSerializerClass(field, serialized);
                            Serializer<?> serializer = getSerializer(getInstanceMethod(serializerClass));

                            if (Modifier.isPrivate(field.getModifiers()))
                                field.setAccessible(true);

                            try {
                                int loadedVal = loaded.get(loadedFieldName);
                                field.set(instance, serializer.load(loadedVal));
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        } else throw fieldIsFinalException(field);
                    }
                }
            }
        }
    }

    public static void save(Object instance) {
        try {
            saveRaw(instance);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void saveRaw(Object instance) throws Exception {
        Field[] fields = instance.getClass().getDeclaredFields();
        for (Field field : fields)
            if (field.isAnnotationPresent(Serialized.class))
                serializeField(instance, field);
    }

    private static <T> void serializeField(Object instance, Field field) throws Exception {
        Serialized serialized = field.getAnnotation(Serialized.class);
        // Get class of the default Serializer
        // either from the parameter in the annotation
        // or from the Default Serializer Map
        Class<? extends Serializer<?>> serializerClass = getSerializerClass(field, serialized);
        Method instanceMethod = getInstanceMethod(serializerClass);
        Serializer<T> serializer = (Serializer<T>) getSerializer(instanceMethod);
        if (Modifier.isPrivate(field.getModifiers()))
            field.setAccessible(true);
        Object i = field.get(instance);
        DataManager.INSTANCE.save(field.getName(), serializer.serialize((T) i));
    }

    private static Class<? extends Serializer<?>> getSerializerClass(Field field, Serialized serialized) throws Exception {
        return switch (serialized.value().length) {
            case 0 -> findDefaultSerializer(field);
            case 1 -> serialized.value()[0];
            default -> throw new Error("Provided more than one serializer for field " + field);
        };
    }

    private static Serializer<?> getSerializer(Method instanceMethod) {
        Serializer<?> serializer = null;
        try {
            Object result = instanceMethod.invoke(null);
            if (result instanceof Serializer<?> serializer1) {
                serializer = serializer1;
            } else {
                // TODO: Throw exception when the returned value is not a valid serializer
            }
            return serializer;
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static Class<? extends Serializer<?>> findDefaultSerializer(Field field) throws Exception {
        Class<? extends Serializer<?>> serializer = Main.DEFAULT_SERIALIZER.get(field.getType());
        if (serializer != null)
            return serializer;
        else
            throw noDefaultSerializer(field);
    }

    private static Exception noDefaultSerializer(Field field) {
        return new Exception("Could not get default serializer for type: " + field.getType().getTypeName() + ". Please provide a serializer manually by passing it to the annotation");
    }

    private static IllegalAccessException fieldIsFinalException(Field field) {
        return new IllegalAccessException("Field: " + field.getName() + " has the final modifier, meaning the saved data cannot be loaded.");
    }

    private static boolean isNotFinal(Field field) {
        return !Modifier.isFinal(field.getModifiers());
    }

    private static Method getInstanceMethod(Class<? extends Serializer<?>> serializerClass) throws Exception {
        Method[] methods = serializerClass.getDeclaredMethods();
        Method instanceMethod = null;
        for (Method method : methods) {
            if (method.isAnnotationPresent(SerializerInstance.class)) {
                if (!Modifier.isStatic(method.getModifiers()))
                    throw instanceMethodNotStatic(method);

                if (method.getReturnType() != serializerClass)
                    throw incorrectReturnTypeException(serializerClass, method);

                if (instanceMethod == null) {
                    instanceMethod = method;
                } else {
                    throw multipleInstancesError(serializerClass, method, instanceMethod);
                }
            }
        }

        if (instanceMethod == null)
            throw noInstanceMethod(serializerClass);

        return instanceMethod;
    }

    private static RuntimeException noInstanceMethod(Class<? extends Serializer<?>> serializerClass) {
        return new RuntimeException("Class: `"
                + serializerClass.getName()
                + "` does not have an instance getter method. Your serializer class needs a public, static method that will return a singleton instance of your main.api.Serializer. This method also needs to be annotated with `@main.api.annotations.SerializerInstance` and have the class as its return type");
    }

    private static IllegalAccessException instanceMethodNotStatic(Method method) {
        return new IllegalAccessException("Method `" + method.getName() + "` needs to be static in order to be a valid instance getter method");
    }

    private static Exception incorrectReturnTypeException(Class<? extends Serializer<?>> serializerClass, Method method) {
        return new Exception("Return type of method `" + method.getName()
                + "` is not correct. Since this method is annotated as an instance getter method it needs to return the type of the instance and therefore it's class. Provided return type: "
                + method.getReturnType().getTypeName()
                + ", expected return type: "
                + serializerClass.getTypeName()
        );
    }

    private static RuntimeException multipleInstancesError(Class<? extends Serializer<?>> serializerClass, Method method, Method instanceMethod) {
        return new RuntimeException("Only one method in the main.api.Serializer class is allowed to have a @main.api.annotations.SerializerInstance annotation. Affected class: `"
                + serializerClass.getName()
                + "`, affected method: `"
                + method.getName()
                + "`, already present instance getter method: `"
                + instanceMethod.getName()
                + "`"
        );
    }
}
