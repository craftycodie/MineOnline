package gg.codie.mineonline.patches.minecraft;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;

import java.lang.reflect.Field;

public class ClockFXAdvice {
    public static double dial;
    public static Field dialField;

    @Advice.OnMethodExit()
    public static void intercept(@Advice.This(typing = Assigner.Typing.DYNAMIC) Object thisObject) {
        try {
            if (dialField == null) {
                for (Field field : thisObject.getClass().getDeclaredFields()) {
                    if (field.getType() == double.class) {
                        dialField = field;
                        break;
                    }
                }
            }
            dial = dialField.getDouble(thisObject);
        } catch (Exception ex) {
            ex.printStackTrace();
            // ignore.
        }
    }
}
