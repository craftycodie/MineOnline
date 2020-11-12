package gg.codie.mineonline.patches.minecraft;

import gg.codie.mineonline.patches.PointerInfoGetLocationAdvice;
import gg.codie.mineonline.patches.RobotMouseMoveAdvice;
import gg.codie.mineonline.patches.lwjgl.*;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;

public class InputPatch {
    public static boolean enableClassicFixes;
    // For classic, keeps track of whether the mouse is grabbed or not pretty much.
    public static boolean isFocused = false;

    public static void init() {
        try {
            new ByteBuddy()
                    .redefine(InputPatch.class.getClassLoader().loadClass("org.lwjgl.input.Mouse"))
                    // Determine whether the cursor is focused.
                    .visit(Advice.to(LWJGLMouseSetNativeCursorAdvice.class).on(ElementMatchers.named("setNativeCursor")))
                    // Lock Delta calls to MineOnline, indev calls it and throws away the result, and it can only be called once per frame.
                    .visit(Advice.to(LWJGLMouseGetDYAdvice.class).on(ElementMatchers.named("getDX")))
                    .visit(Advice.to(LWJGLMouseGetDYAdvice.class).on(ElementMatchers.named("getDY")))
                    // Prevent clicks from doubling.
                    .visit(Advice.to(LWJGLMouseIsButtonDownAdvice.class).on(ElementMatchers.named("isButtonDown").and(ElementMatchers.takesArguments(int.class))))
                    // Prevent mouse centering when the MO menu is open.
                    .visit(Advice.to(LWJGLSetCursorLocationAdvice.class).on(ElementMatchers.named("setCursorLocation")))
                    // Prevent placing/breaking when the MO menu is open.
                    .visit(Advice.to(LWJGLInputEventAdvice.class).on(ElementMatchers.named("getEventButtonState")))
                    .visit(Advice.to(LWJGLInputEventAdvice.class).on(ElementMatchers.named("getX")))
                    .visit(Advice.to(LWJGLInputEventAdvice.class).on(ElementMatchers.named("getY")))
                    .visit(Advice.to(LWJGLInputEventAdvice.class).on(ElementMatchers.named("next")))
                    .make()
                    .load(Class.forName("org.lwjgl.input.Mouse").getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());

            new ByteBuddy()
                    .redefine(InputPatch.class.getClassLoader().loadClass("org.lwjgl.input.Keyboard"))
                    .visit(Advice.to(LWJGLKeyboardGetEventKeyStateAdvice.class).on(ElementMatchers.named("getEventKeyState")))
                    .make()
                    .load(Class.forName("org.lwjgl.input.Keyboard").getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());


            new ByteBuddy()
                    .redefine(InputPatch.class.getClassLoader().loadClass("org.lwjgl.input.Cursor"))
                    // Allow a cursor to be created whether compatible or not, it's never used but it's existence is queried.
                    .visit(Advice.to(LWJGLCursorGetCapabilitiesAdvice.class).on(ElementMatchers.named("getCapabilities")))
                    .make()
                    .load(Class.forName("org.lwjgl.input.Cursor").getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());

            new ByteBuddy()
                    .redefine(InputPatch.class.getClassLoader().loadClass("java.awt.PointerInfo"))
                    // Used in getting the mouse movements.
                    .visit(Advice.to(PointerInfoGetLocationAdvice.class).on(ElementMatchers.named("getLocation")))
                    .make()
                    .load(Class.forName("java.awt.PointerInfo").getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());

            new ByteBuddy()
                    .redefine(InputPatch.class.getClassLoader().loadClass("java.awt.Robot"))
                    // Prevent mouse centering when the MO menu is open.
                    .visit(Advice.to(RobotMouseMoveAdvice.class).on(ElementMatchers.named("mouseMove")))
                    .make()
                    .load(Class.forName("java.awt.Robot").getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}
