package gg.codie.mineonline.patches.minecraft;

import gg.codie.mineonline.LauncherFiles;
import gg.codie.mineonline.Settings;
import gg.codie.mineonline.client.LegacyGameManager;
import gg.codie.mineonline.patches.ReturnTrueAdvice;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ColorizerPatch {
    public static void init() {
        if (LegacyGameManager.getVersion() != null && LegacyGameManager.getVersion().grassColorizerClass != null) {
            try {
                new ByteBuddy()
                        .redefine(ClassLoader.getSystemClassLoader().loadClass(LegacyGameManager.getVersion().grassColorizerClass))
                        .visit(Advice.to(GrassColorizerAdvice.class).on(ElementMatchers.takesArguments(
                                double.class, double.class
                        )))
                        .make()
                        .load(ClassLoader.getSystemClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
            } catch (ClassNotFoundException ex) {
                // If the lib isn't loaded the version must not need it, no need to patch it.
            }
        }

        if (LegacyGameManager.getVersion() != null && LegacyGameManager.getVersion().foliageColorizerClass != null) {
            try {
                new ByteBuddy()
                        .redefine(ClassLoader.getSystemClassLoader().loadClass(LegacyGameManager.getVersion().foliageColorizerClass))
                        .visit(Advice.to(FoliageColorizerAdvice.class).on(ElementMatchers.takesArguments(
                                double.class, double.class
                        )))
                        .make()
                        .load(ClassLoader.getSystemClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
            } catch (ClassNotFoundException ex) {
                // If the lib isn't loaded the version must not need it, no need to patch it.
            }
        }

        if (LegacyGameManager.getVersion() != null && LegacyGameManager.getVersion().waterColorizerClass != null) {
            try {
                new ByteBuddy()
                        .redefine(ClassLoader.getSystemClassLoader().loadClass(LegacyGameManager.getVersion().waterColorizerClass))
                        .visit(Advice.to(WaterColorizerAdvice.class).on(ElementMatchers.takesArguments(
                                double.class, double.class
                        )))
                        .make()
                        .load(ClassLoader.getSystemClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
            } catch (ClassNotFoundException ex) {
                // If the lib isn't loaded the version must not need it, no need to patch it.
            }
        }
    }

    public static void updateColorizers() {
        if (LegacyGameManager.getVersion() != null && LegacyGameManager.getVersion().grassColorizerClass != null)
            GrassColorizerAdvice.updateColorizer();
        if (LegacyGameManager.getVersion() != null && LegacyGameManager.getVersion().foliageColorizerClass != null)
            FoliageColorizerAdvice.updateColorizer();
        if (LegacyGameManager.getVersion() != null && LegacyGameManager.getVersion().waterColorizerClass != null)
            WaterColorizerAdvice.updateColorizer();
    }
}
