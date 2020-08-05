package gg.codie.mineonline.patches;

import gg.codie.mineonline.Globals;
import net.bytebuddy.asm.Advice;

import java.lang.reflect.Field;

class EnvironmentConstructAdvice {
    @Advice.OnMethodEnter
    static void intercept(@Advice.Argument(0) String auth, @Advice.Argument(1) String account, @Advice.Argument(2) String session, @Advice.Argument(3) String name) {
        System.out.println("Environment: " + auth + ", " + account + ", " + session + ", " + name);
        try {

            Field f = String.class.getDeclaredField("value");
            f.setAccessible(true);
            f.set(auth, ("http://" + Globals.API_HOSTNAME).toCharArray());
            f.set(account, ("http://" + Globals.API_HOSTNAME).toCharArray());
            f.set(session, ("http://" + Globals.API_HOSTNAME).toCharArray());
            f.set(name, "MineOnline".toCharArray());

        } catch (Exception ex) {
            ex.printStackTrace();
        } catch (NoClassDefFoundError error) {
            error.printStackTrace();
            // ionore
        }
    }
}