package gg.codie.mineonline.patches.minecraft;

import gg.codie.mineonline.Settings;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.matcher.ElementMatchers;

import java.net.URLClassLoader;

public class FOVPatch {
    public static void useCustomFOV(String fovModifierMethod, URLClassLoader classLoader) {
        if (fovModifierMethod == null)
            return;

        FOVGetModifiedAdvice.customFOV = Settings.settings.optFloat(Settings.FOV, 70);


        if (FOVGetModifiedAdvice.customFOV == 70)
            return;

        String className = fovModifierMethod.substring(0, fovModifierMethod.lastIndexOf("." ));
        String methodName = fovModifierMethod.substring(fovModifierMethod.lastIndexOf(".") + 1);

        try {
            Class fovClass = classLoader.loadClass(className);
            System.out.println(fovClass);

            new ByteBuddy()
                    .with(Implementation.Context.Disabled.Factory.INSTANCE)
                    .redefine(classLoader.loadClass(className))
                    .visit(Advice.to(FOVGetModifiedAdvice.class).on(ElementMatchers.named(methodName).and(ElementMatchers.takesArguments(
                            float.class
                    ))))
                    .make()
                    .load(classLoader, ClassReloadingStrategy.fromInstalledAgent());
        } catch (Exception ex) {
            System.err.println("Failed to patch fov.");
            ex.printStackTrace();
        }
    }
}
