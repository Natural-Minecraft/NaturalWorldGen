package id.naturalsmp.NaturalWorldGen.util.reflect;

import id.naturalsmp.NaturalWorldGen.NaturalGenerator;

import java.lang.reflect.Field;

public class WrappedField<C, T> {

    private final Field field;

    public WrappedField(Class<C> origin, String methodName) {
        Field f = null;
        try {
            f = origin.getDeclaredField(methodName);
            f.setAccessible(true);
        } catch (NoSuchFieldException e) {
            NaturalGenerator.error("Failed to created WrappedField %s#%s: %s%s", origin.getSimpleName(), methodName, e.getClass().getSimpleName(), e.getMessage().equals("") ? "" : " | " + e.getMessage());
        }
        this.field = f;
    }

    public T get() {
        return get(null);
    }

    public T get(C instance) {
        if (field == null) {
            return null;
        }

        try {
            return (T) field.get(instance);
        } catch (IllegalAccessException e) {
            NaturalGenerator.error("Failed to get WrappedField %s#%s: %s%s", field.getDeclaringClass().getSimpleName(), field.getName(), e.getClass().getSimpleName(), e.getMessage().equals("") ? "" : " | " + e.getMessage());
            return null;
        }
    }

    public void set(T value) throws IllegalAccessException {
        set(null, value);
    }

    public void set(C instance, T value) throws IllegalAccessException {
        if (field == null) {
            return;
        }

        field.set(instance, value);
    }

    public boolean hasFailed() {
        return field == null;
    }
}
