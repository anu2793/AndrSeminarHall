package vn.hanelsoft.utils;
import java.util.Random;

/**
 * Created by macmobiles on 3/21/2016.
 */
public class RandomUtils {
    public enum StringType {
        ALPHA_NUMERIC("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"),
        NUMERIC("0123456789"),
        ALPHABET("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");

        private String type;

        StringType(String type) {
            this.type = type;
        }
    }

    private static final Random random = new Random();

    public static String nextString(int length) {
        return nextString(length, StringType.ALPHA_NUMERIC);
    }

    public static String nextString(int length, StringType type) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++)
            sb.append(type.type.charAt(random.nextInt(type.type.length())));
        return sb.toString();
    }

    public static int nextInt(int seed) {
        return random.nextInt(seed);
    }

    /**
     *@see Random#nextFloat
     */
    public static float nextFloat(){
        return random.nextFloat();
    }
    public static boolean nextBoolean() {
        return random.nextBoolean();
    }
}
