package gg.codie.mineonline.patches;

import net.bytebuddy.asm.Advice;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ByteBufferPutAdvice {
    public static String username;

    @Advice.OnMethodEnter
    public static void intercept(@Advice.Argument(value = 0, readOnly = false) byte[] bytes) {
        if (bytes.length != 64)
            return;

        byte[] guestBytes = new byte[64];
        guestBytes[0] = 0x67; //g
        guestBytes[1] = 0x75; //u
        guestBytes[2] = 0x65; //e
        guestBytes[3] = 0x73; //s
        guestBytes[4] = 0x74; //t

        for (int i = 5; i < 64; i++)
            guestBytes[i] = 0x20;

        if (Arrays.equals(bytes, guestBytes)) {
            try {
                String username = (String) ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.ByteBufferPutAdvice").getField("username").get(null);
                bytes = Arrays.copyOf(username.getBytes(StandardCharsets.UTF_8), 64);
                for (int i = username.length(); i < 64; i++)
                    bytes[i] = 0x20;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
