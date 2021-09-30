package gg.codie.mineonline.patches.minecraft;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;

public class PCGamerDemoPatch {
    public static void unlockDemo() {
        try {
            new ByteBuddy()
                    .redefine(Class.forName("fd"))
                    .visit(Advice.to(PCGamerDemoAdvice.class).on(ElementMatchers.isConstructor()))
                    .make()
                    .load(ClassLoader.getSystemClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}
