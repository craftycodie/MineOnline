package gg.codie.mineonline.patches.minecraft;

import gg.codie.mineonline.Settings;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;

public class GuiScreenPatch {
    public static void useGUIScale(String guiScreenClassName) {
        try {
            GuiScreenOpenAdvice.guiScale = Settings.settings.optInt(Settings.GUI_SCALE, 0);

            new ByteBuddy()
                    .redefine(Class.forName(guiScreenClassName))
                    .visit(Advice.to(GuiScreenOpenAdvice.class).on(ElementMatchers.isPublic().and(ElementMatchers.takesArgument(1, int.class).and(ElementMatchers.takesArgument(2, int.class)))))
                    .make()
                    .load(ClassLoader.getSystemClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}
