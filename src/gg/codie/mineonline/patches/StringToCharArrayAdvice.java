package gg.codie.mineonline.patches;

import net.bytebuddy.asm.Advice;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class StringToCharArrayAdvice {
    public static String versionString;

    @Advice.OnMethodExit
    public static void intercept(@Advice.This String thisObj, @Advice.Return(readOnly = false) char[] returnObj) {
        try {
            // Version strings always start with Minecraft or 0.
            if (thisObj.startsWith("Minecraft") || thisObj.startsWith("0.")) {
                String versionString = (String) ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.StringToCharArrayAdvice").getField("versionString").get(null);
                if(versionString != null) {
                    // Comparing them as strings doens't play well with the patch.
                    if (Arrays.equals(thisObj.getBytes(), versionString.getBytes())) {
                        returnObj = new char[]{' '};
                    }
                }
            }

            if (thisObj.contains(":")) {
                String replacedString = thisObj.replace(":heart:", "\u0003");
                replacedString = replacedString.replace(":smile:", "\u0002");
                replacedString = replacedString.replace(":male_sign:", "\u000B");
                replacedString = replacedString.replace(":female_sign:", "\u000C");
                replacedString = replacedString.replace(":musical_note:", "\u000E");
                byte[] bytes = replacedString.getBytes(StandardCharsets.UTF_8);
                char[] chars = new char[bytes.length];
                for (int i = 0; i < bytes.length; i++)
                    chars[i] = (char) bytes[i];

                returnObj = chars;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
