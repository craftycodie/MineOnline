package gg.codie.mineonline.patches;

import net.bytebuddy.asm.Advice;

public class SystemSetPropertyAdvice {
    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    static boolean intercept(@Advice.Argument(0) String property) {
        return property.equals("org.lwjgl.librarypath") || property.equals("net.java.games.input.librarypath");
    }
}
