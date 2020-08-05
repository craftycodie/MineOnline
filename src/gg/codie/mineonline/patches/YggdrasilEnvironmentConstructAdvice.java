package gg.codie.mineonline.patches;

import gg.codie.mineonline.Globals;
import net.bytebuddy.asm.Advice;

import java.lang.reflect.Field;

class YggdrasilEnvironmentConstructAdvice {
    @Advice.OnMethodEnter
    static void intercept(@Advice.Argument(0) String auth, @Advice.Argument(1) String account, @Advice.Argument(2) String session) {
        System.out.println("Environment: " + auth + ", " + account + ", " + session);
        try {

            Field f = String.class.getDeclaredField("value");
            f.setAccessible(true);
            f.set(auth, ("http://" + Globals.API_HOSTNAME).toCharArray());
            f.set(account, ("http://" + Globals.API_HOSTNAME).toCharArray());
            f.set(session, ("http://" + Globals.API_HOSTNAME).toCharArray());

        } catch (Exception ex) {
            ex.printStackTrace();
        } catch (NoClassDefFoundError error) {
            error.printStackTrace();
            // ionore
        }
    }
}