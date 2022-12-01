package gg.codie.mineonline.patches.minecraft;

import gg.codie.common.utils.OSUtils;
import gg.codie.mineonline.Globals;
import gg.codie.mineonline.MinecraftVersionRepository;
import gg.codie.mineonline.client.LegacyGameManager;
import jdk.nashorn.internal.objects.Global;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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
                if (OSUtils.isM1System()) {
                    new ByteBuddy()
                            .redefine(ClassLoader.getSystemClassLoader().loadClass(LegacyGameManager.getVersion().foliageColorizerClass))
                            .visit(Advice.to(FoliageColorizerAdvice.class).on(ElementMatchers.takesArguments(
                                    double.class, double.class
                            )))
                            .visit(Advice.to(FoliageColorizerM1Advice.class).on(ElementMatchers.named("b").and(ElementMatchers.takesNoArguments()).and(ElementMatchers.isPublic()).and(ElementMatchers.isStatic())))
                            .make()
                            .load(ClassLoader.getSystemClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
                } else {
                    new ByteBuddy()
                            .redefine(ClassLoader.getSystemClassLoader().loadClass(LegacyGameManager.getVersion().foliageColorizerClass))
                            .visit(Advice.to(FoliageColorizerAdvice.class).on(ElementMatchers.takesArguments(
                                    double.class, double.class
                            )))
                            .make()
                            .load(ClassLoader.getSystemClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
                }
            } catch (ClassNotFoundException ex) {
                //
            }
        } else if (LegacyGameManager.getVersion() != null && OSUtils.isM1System()) {
            // Birch was added in beta so only apply this patch on beta + release.
            if (!LegacyGameManager.getVersion().baseVersion.startsWith("1.") && !LegacyGameManager.getVersion().baseVersion.startsWith("b1."))
                return;

            MinecraftVersionRepository repository = new MinecraftVersionRepository(null);
            repository.getLastSelectedJarPath();

            try {
                JarFile jar = new JarFile(repository.getLastSelectedJarPath());
                Enumeration<JarEntry> enumEntries = jar.entries();
                // Reversing the enumeration avoids some nasty static initializers found in release.
                LinkedList<JarEntry> jarEntries = new LinkedList<>();
                while (enumEntries.hasMoreElements()) {
                    jarEntries.add(enumEntries.nextElement());
                }
                // Hacky way to avoid some bad static initializers in release.
                if (!LegacyGameManager.getVersion().baseVersion.startsWith("b"))
                    jarEntries.sort((f, s) -> s.getName().compareTo(f.getName()));
                for (JarEntry file : jarEntries) {
                    String fileName = file.getName();
                    if (file.isDirectory() || !fileName.endsWith(".class") || fileName.contains("/")) {
                        continue;
                    }

                    try {
                        Class<?> clazz = Class.forName(file.getName().replace(".class", "").replace("/", "."));

                        // We check for a class with matching methods and fields to the foliage colorizer.
                        Field pixelsField = clazz.getDeclaredField("a");
                        // not in b1.5_01 for some reason
                        Method loadPixels = null;
                        try {
                             loadPixels = clazz.getDeclaredMethod("a", int[].class);
                        } catch (NoSuchMethodException ex) {
                            //
                        }
                        Method pixelMethod = clazz.getDeclaredMethod("a", double.class, double.class);
                        Method spruceMethod = clazz.getDeclaredMethod("a");
                        Method birchMethod = clazz.getDeclaredMethod("b");
                        // not in b1.5_01 for some reason
                        Method unkMethod = null;
                        try {
                            unkMethod = clazz.getDeclaredMethod("c");
                        } catch (NoSuchMethodException ex) {
                            //
                        }
                        // There's also a static constructor, idk how to get it.

                        // Now a bunch of assertions
                        if (pixelsField.getType() != int[].class) continue;
                        if (!Modifier.isPrivate(pixelsField.getModifiers()) || !Modifier.isStatic(pixelsField.getModifiers()))
                            continue;
                        if (loadPixels != null && (!Modifier.isPublic(loadPixels.getModifiers()) || !Modifier.isStatic(loadPixels.getModifiers())))
                            continue;
                        if (!Modifier.isPublic(pixelMethod.getModifiers()) || !Modifier.isStatic(pixelMethod.getModifiers()))
                            continue;
                        if (!Modifier.isPublic(spruceMethod.getModifiers()) || !Modifier.isStatic(spruceMethod.getModifiers()))
                            continue;
                        if (!Modifier.isPublic(birchMethod.getModifiers()) || !Modifier.isStatic(birchMethod.getModifiers()))
                            continue;
                        if (unkMethod != null && (!Modifier.isPublic(unkMethod.getModifiers()) || !Modifier.isStatic(unkMethod.getModifiers())))
                            continue;

                        if (Globals.DEV)
                            System.out.println("Foliage Found " + clazz.getCanonicalName());

                        // Ok this is probably the one.
                        new ByteBuddy()
                                .redefine(ClassLoader.getSystemClassLoader().loadClass(clazz.getName()))
                                .visit(Advice.to(FoliageColorizerM1Advice.class).on(ElementMatchers.named("b").and(ElementMatchers.takesNoArguments()).and(ElementMatchers.isPublic()).and(ElementMatchers.isStatic())))
                                .make()
                                .load(ClassLoader.getSystemClassLoader(), ClassReloadingStrategy.fromInstalledAgent());

                        break;
                    } catch (NoSuchMethodException | NoSuchFieldException ex) {
                        continue;
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
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
