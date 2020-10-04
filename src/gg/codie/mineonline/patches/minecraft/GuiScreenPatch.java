package gg.codie.mineonline.patches.minecraft;

import gg.codie.mineonline.Settings;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;

import java.net.URLClassLoader;

public class GuiScreenPatch {
    public static void useGUIScale(String guiScreenClassName, URLClassLoader classLoader) {
        try {
            GuiScreenOpenAdvice.guiScale = Settings.settings.optInt(Settings.GUI_SCALE, 0);

            new ByteBuddy()
                    .redefine(classLoader.loadClass(guiScreenClassName))
                    .visit(Advice.to(GuiScreenOpenAdvice.class).on(ElementMatchers.isPublic().and(ElementMatchers.takesArgument(1, int.class).and(ElementMatchers.takesArgument(2, int.class)))))
                    .make()
                    .load(classLoader, ClassReloadingStrategy.fromInstalledAgent());
        } catch (ClassNotFoundException ex) {
            // If the lib isn't loaded the version must not need it, no need to patch it.
        }
    }
}
