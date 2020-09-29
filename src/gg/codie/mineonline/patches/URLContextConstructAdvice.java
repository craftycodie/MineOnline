package gg.codie.mineonline.patches;

import gg.codie.mineonline.Globals;
import net.bytebuddy.asm.Advice;

import java.lang.reflect.Field;
import java.net.URL;

public class URLContextConstructAdvice {
    @Advice.OnMethodEnter
    static void intercept(@Advice.Argument(value = 0, readOnly = false) URL context, @Advice.Argument(1) String spec) {
        try {
            String updateURL = (String)ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.URLConstructAdvice").getField("updateURL").get(null);
            if (updateURL != null && spec.matches("minecraft.jar\\?user=([^<]*)&ticket=deprecated")) {
                context = new URL(updateURL.substring(0, updateURL.lastIndexOf("/") + 1));
                Field f = String.class.getDeclaredField("value");
                f.setAccessible(true);
                f.set(spec, updateURL.substring(updateURL.lastIndexOf("/") + 1).toCharArray());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}