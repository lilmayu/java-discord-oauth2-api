package dev.mayuna.discord;

import org.junit.jupiter.api.Assertions;

import java.lang.reflect.Field;

public class Utils {

    public static void AssertStringArraysEquals(String[] a, String[] b) {
        if (a.length != b.length) {
            Assertions.fail("Arrays are not the same length (a.length != b.length: " + a.length + " != " + b.length + ")");
        }

        for (int i = 0; i < a.length; i++) {
            if (!a[i].equals(b[i])) {
                Assertions.fail("Arrays are not the same (a[i] != b[i]: " + a[i] + " != " + b[i] + ")");
            }
        }
    }

    public static void setField(Object object, String fieldName, Object value) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
