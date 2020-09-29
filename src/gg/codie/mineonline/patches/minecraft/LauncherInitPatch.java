package gg.codie.mineonline.patches.minecraft;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.matcher.ElementMatchers;

import java.net.URLClassLoader;

public class LauncherInitPatch {
    public static void allowCustomUpdates(String latestVersion, URLClassLoader classLoader) {
        LauncherInitAdvice.latestVersion = latestVersion;

        try {
            new ByteBuddy()
                    .with(Implementation.Context.Disabled.Factory.INSTANCE)
                    .redefine(classLoader.loadClass("net.minecraft.Launcher"))
                    .visit(Advice.to(LauncherInitAdvice.class).on(ElementMatchers.named("init").and(ElementMatchers.takesArguments(
                            String.class, //username
                            String.class, //latest version
                            String.class, //download ticket
                            String.class  //session
                    ))))
                    .make()
                    .load(ClassLoader.getSystemClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
        } catch (Exception ex) {
            System.err.println("Failed to patch minecraft launcher.");
            ex.printStackTrace();
        }
    }
}
