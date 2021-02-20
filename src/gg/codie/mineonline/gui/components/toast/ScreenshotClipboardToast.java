package gg.codie.mineonline.gui.components.toast;

import gg.codie.mineonline.Settings;
import gg.codie.mineonline.client.LegacyGameManager;
import org.lwjgl.input.Keyboard;

public class ScreenshotClipboardToast implements IToast {
    @Override
    public String getLine1() {
        return "Screenshots are automatically";
    }

    @Override
    public String getLine2() {
        return "copied to your clipboard :)";
    }

    @Override
    public boolean isActive() {
        if (Keyboard.isKeyDown(Keyboard.KEY_F2) && Settings.singleton.getScreenshotToast()) {
            Settings.singleton.setScreenshotToast(false);
            Settings.singleton.saveSettings();
            return true;
        }
        return false;
    }
}
