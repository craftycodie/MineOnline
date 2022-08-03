package gg.codie.mineonline.gui.components.toast;

import gg.codie.mineonline.Settings;
import gg.codie.mineonline.client.LegacyGameManager;
import org.lwjgl.input.Keyboard;

public class ZoomToast implements IToast {
    @Override
    public String getLine1() {
        return "Press the " + Keyboard.getKeyName(Settings.singleton.getZoomKeyCode()) + " key to zoom in";
    }

    @Override
    public String getLine2() {
        return "";
    }

    @Override
    public boolean isActive() {
        if (!LegacyGameManager.isInGame() || Settings.singleton.getZoomKeyCode() == 0)
            return false;
        return Settings.singleton.getZoomToast();
    }
}
