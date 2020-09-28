package gg.codie.mineonline.patches.minecraft;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.matcher.ElementMatchers;

import java.util.HashMap;

public class LauncherInitPatch {
    public static void allowCustomUpdates(String latestVersion) {
        LauncherInitAdvice.latestVersion = latestVersion;

        try {
            new ByteBuddy()
                    .with(Implementation.Context.Disabled.Factory.INSTANCE)
                    .redefine(Class.forName("net.minecraft.Launcher"))
                    .visit(Advice.to(LauncherInitAdvice.class).on(ElementMatchers.named("init").and(ElementMatchers.takesArguments(
                            String.class, //username
                            String.class, //latest version
                            String.class, //download ticket
                            String.class  //session
                    ))))
                    .make()
                    .load(Class.forName("net.minecraft.Launcher").getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
        } catch (Exception ex) {
            System.err.println("Failed to patch minecraft launcher.");
            ex.printStackTrace();
        }
    }
}
