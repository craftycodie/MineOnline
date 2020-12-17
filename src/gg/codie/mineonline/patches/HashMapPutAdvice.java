package gg.codie.mineonline.patches;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.matcher.ElementMatchers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class HashMapPutAdvice {
    public static HashMap<String, Integer> textures = new HashMap<>();
    public static HashMap<String, ArrayList> sounds = new HashMap<>();
    public static boolean ignore;

    @Advice.OnMethodExit
    static void intercept(@Advice.Argument(0) Object key, @Advice.Argument(1) Object value, @Advice.This(typing = Assigner.Typing.DYNAMIC) Object thisObj) {
        if(!(key instanceof String && (value instanceof Integer || value instanceof ArrayList)) || !((String)key).endsWith(".png") && !((String)key).contains("calm"))
            return;

        try {
            if ((boolean)ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.HashMapPutAdvice").getField("ignore").get(null))
                return;

            if ((boolean)ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.lwjgl.LWJGLDisplayUpdateAdvice").getField("inUpdateHook").get(null))
                return;

            List<String> ignored = Arrays.asList(new String[] {
                    "/custom_water_flowing.png",
                    "/custom_water_still.png",
                    "/custom_lava_flowing.png",
                    "/custom_lava_still.png",
                    "/custom_portal.png",
                    "/custom_fire_e_w.png",
                    "/custom_fire_n_s.png"
            });

            if (ignored.contains(key))
                return;

            System.out.println(key);


//            System.out.println("RELOADING " + key);

            ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.HashMapPutAdvice").getField("ignore").set(null, true);
            boolean DEV = (boolean) ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.Globals").getField("DEV").get(null);

            if (DEV)
                System.out.println("Loaded Texture " + key + " at " + value);


            if (((String) key).endsWith(".png")) {
                HashMap<String, Integer> textures = (HashMap<String, Integer>) ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.HashMapPutAdvice").getField("textures").get(null);

                textures.put((String) key, (Integer) value);

                ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.HashMapPutAdvice").getField("ignore").set(null, false);

                ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.gui.rendering.Loader").getDeclaredMethod("reloadMinecraftTexture", String.class).invoke(null, new Object[] { key });
            } else if (key.equals("calm") && value instanceof ArrayList) {
                ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.HashMapPutAdvice").getField("sounds").set(null, thisObj);
                ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.HashMapPutAdvice").getField("ignore").set(null, false);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
