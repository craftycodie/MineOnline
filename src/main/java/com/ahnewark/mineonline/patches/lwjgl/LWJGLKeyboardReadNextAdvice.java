package com.ahnewark.mineonline.patches.lwjgl;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;

import java.lang.reflect.Field;

public class LWJGLKeyboardReadNextAdvice {
    public static int position = 0;

    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    static boolean skip() {
        try {
            if (
                    (boolean) ClassLoader.getSystemClassLoader().loadClass("com.ahnewark.mineonline.client.LegacyGameManager").getMethod("mineonlineMenuOpen").invoke(null)
                            && !(boolean) ClassLoader.getSystemClassLoader().loadClass("com.ahnewark.mineonline.patches.lwjgl.LWJGLDisplayUpdateAdvice").getField("inUpdateHook").get(null)
            ) {
                return true;
            }
        } catch (Exception ex) {

        }

        return false;
    }

    @Advice.OnMethodExit()
    static void lockCalls(@Advice.Argument(value = 0, readOnly = false, typing = Assigner.Typing.DYNAMIC) Object keyEvent, @Advice.Return(readOnly = false) boolean returnEvent) {
        try {
            Field positionField = ClassLoader.getSystemClassLoader().loadClass("com.ahnewark.mineonline.patches.lwjgl.LWJGLKeyboardReadNextAdvice").getField("position");

            if (
                    (boolean) ClassLoader.getSystemClassLoader().loadClass("com.ahnewark.mineonline.client.LegacyGameManager").getMethod("mineonlineMenuOpen").invoke(null)
                            && !(boolean) ClassLoader.getSystemClassLoader().loadClass("com.ahnewark.mineonline.patches.lwjgl.LWJGLDisplayUpdateAdvice").getField("inUpdateHook").get(null)
            ){
                if((int)positionField.get(null) < 256) {
                    Field stateField = keyEvent.getClass().getDeclaredField("state");
                    Field keyField = keyEvent.getClass().getDeclaredField("key");
                    Field repeatField = keyEvent.getClass().getDeclaredField("repeat");

                    stateField.setAccessible(true);
                    keyField.setAccessible(true);
                    repeatField.setAccessible(true);

                    stateField.set(keyEvent, false);
                    keyField.set(keyEvent, positionField.get(null));
                    repeatField.set(keyEvent, false);

                    returnEvent = true;

                    positionField.set(null, (int)positionField.get(null) + 1);

                    return;
                }
            } else {
                positionField.set(null, 0);
            }
        } catch (Exception ex) {
            try {
                boolean DEV = (boolean)ClassLoader.getSystemClassLoader().loadClass("com.ahnewark.mineonline.Globals").getField("DEV").get(null);
                if (DEV)
                    ex.printStackTrace();
            } catch (Exception ex2) { }
        }
    }
}
