package gg.codie.mineonline.patches.minecraft;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;

public class FOVViewmodelPatch {
    public static void fixViewmodelFOV(String entityRendererClassName, String viewModelFunctionName) {
        try {
            new ByteBuddy()
                    .redefine(Class.forName(entityRendererClassName))
                    .visit(Advice.to(FOVViewmodelAdvice.class).on(ElementMatchers.named(viewModelFunctionName).and(ElementMatchers.takesArguments(
                            float.class,
                            int.class
                    ))))
                    .make()
                    .load(ClassLoader.getSystemClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
        } catch (ClassNotFoundException ex) {
            // If the lib isn't loaded the version must not need it, no need to patch it.
        }
    }
}
