package gg.codie.mineonline.utils;

public class MathUtils {

    public static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }

    public static int clamp(int val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }

    public static double mod(double i, double j)
    {
        if (i < 0) {
            return -(-i % j);
        } else {
            return i % j;
        }
    }

}
