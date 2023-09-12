package com.ahnewark.mineonline.gui.components.toast;

import com.ahnewark.mineonline.Settings;
import com.ahnewark.mineonline.client.LegacyGameManager;
import org.lwjgl.input.Keyboard;

public class MenuToast implements IToast {
    @Override
    public String getLine1() {
        return "Press the " + Keyboard.getKeyName(Settings.singleton.getMineonlineMenuKeyCode()) + " key to open";
    }

    @Override
    public String getLine2() {
        return "the MineOnline menu.";
    }

    @Override
    public boolean isActive() {
        if (!LegacyGameManager.isInGame() || LegacyGameManager.getVersion() == null || !LegacyGameManager.getVersion().useMineOnlineMenu)
            return false;
        return Settings.singleton.getMenuToast();
    }
}
