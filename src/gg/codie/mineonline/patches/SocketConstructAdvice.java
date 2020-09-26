package gg.codie.mineonline.patches;

import gg.codie.mineonline.Globals;
import net.bytebuddy.asm.Advice;

import java.lang.reflect.Field;
import java.net.InetAddress;

public class SocketConstructAdvice {
    @Advice.OnMethodEnter
    static void intercept(@Advice.Argument(value = 0) InetAddress ip, @Advice.Argument(value = 1) int port) {
       System.out.println("Joined Server " + ip.getHostAddress() + ":" + port);
    }

}
