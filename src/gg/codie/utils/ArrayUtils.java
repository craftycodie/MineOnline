package gg.codie.utils;

import java.lang.reflect.Array;

public class ArrayUtils {

    public static <T> T[] concatenate(T[] a, T[] b) {
        int aLen = a.length;
        int bLen = b.length;

        @SuppressWarnings("unchecked")
        T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);

        return c;
    }

    public static String[] fromString(String string) {
        String[] strings = string.replace("[", "").replace("]", "").split(", ");
        return strings;
    }
}
