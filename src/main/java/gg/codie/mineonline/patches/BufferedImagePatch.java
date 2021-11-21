package gg.codie.mineonline.patches;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.matcher.ElementMatchers;

import java.awt.image.BufferedImage;

public class BufferedImagePatch {
    public static void fixM1() {
        new ByteBuddy()
                .with(Implementation.Context.Disabled.Factory.INSTANCE)
                .redefine(BufferedImage.class)
                .visit(Advice.to(BufferedImageGetRGBM1Advice.class).on(ElementMatchers.named("getRGB").and(ElementMatchers.takesArguments(
                        int.class, int.class, int.class, int.class, int[].class, int.class, int.class
                ))))
                .make()
                .load(BufferedImage.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
    }

    public static void reset() {
        new ByteBuddy()
                .with(Implementation.Context.Disabled.Factory.INSTANCE)
                .redefine(BufferedImage.class)
                .make()
                .load(BufferedImage.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
    }
}
