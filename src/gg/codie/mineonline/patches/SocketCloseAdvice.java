package gg.codie.mineonline.patches;

import net.bytebuddy.asm.Advice;

import java.net.InetAddress;
import java.net.Socket;

public class SocketCloseAdvice {
    @Advice.OnMethodEnter
    static void intercept(@Advice.This Socket thisObject) {
        System.out.println("Left Server " + thisObject.getInetAddress().getHostAddress() + ":" + thisObject.getPort());
    }
}
