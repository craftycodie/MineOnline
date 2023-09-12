package com.ahnewark.mineonline.patches;

import com.ahnewark.mineonline.LauncherFiles;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.matcher.ElementMatchers;

import java.io.File;

public class FilePatch {
    public static String resourcesVersion;
    public static String gameFolder;

    public static void relocateFiles(String resourcesVersion, String gameFolder) {
        FilePatch.resourcesVersion = resourcesVersion;
        FilePatch.gameFolder = gameFolder;

        new File(LauncherFiles.MINEONLINE_RESOURCES_PATH + resourcesVersion + File.separator).mkdirs();
        if (!gameFolder.isEmpty())
            new File(gameFolder).mkdirs();

        new ByteBuddy()
                .with(Implementation.Context.Disabled.Factory.INSTANCE)
                .redefine(File.class)
                .visit(Advice.to(FileConstructAdvice.class).on(ElementMatchers.isConstructor().and(ElementMatchers.takesArguments(
                        String.class
                ))))
                .visit(Advice.to(FileConstructWithParentAdvice.class).on(ElementMatchers.isConstructor().and(ElementMatchers.takesArguments(
                        File.class,
                        String.class
                ))))
                .visit(Advice.to(FileConstructWithStringParentAdvice.class).on(ElementMatchers.isConstructor().and(ElementMatchers.takesArguments(
                        String.class,
                        String.class
                ))))
                .make()
                .load(File.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
    }
}
