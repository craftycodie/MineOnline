package com.ahnewark.mineonline.gui.components.toast;

public class DeviceCodeClipboardToast implements IToast {
    @Override
    public String getLine1() {
        return "Your device code has been";
    }

    @Override
    public String getLine2() {
        return "copied to your clipboard.";
    }

    private static boolean show = false;

    public static void show() {
       show = true;
    }

    @Override
    public boolean isActive() {
        if (show) {
            show = false;
            return true;
        }
        return false;
    }
}
