package gg.codie.mineonline.patches;

import gg.codie.mineonline.LauncherFiles;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.matcher.ElementMatchers;

import java.io.File;

public class FilePatch {
    public static String resourcesVersion;

    public static void relocateFiles(String resourcesVersion) {
        FilePatch.resourcesVersion = resourcesVersion;

        new File(LauncherFiles.MINEONLINE_RESOURCES_PATH + resourcesVersion + File.separator).mkdirs();

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
                .make()
                .load(File.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
    }
}
