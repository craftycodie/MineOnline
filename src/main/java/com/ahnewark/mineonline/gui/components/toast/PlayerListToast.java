package com.ahnewark.mineonline.gui.components.toast;

import com.ahnewark.mineonline.Settings;
import com.ahnewark.mineonline.client.LegacyGameManager;
import com.ahnewark.mineonline.gui.PlayerList;
import org.lwjgl.input.Keyboard;

public class PlayerListToast implements IToast {
    @Override
    public String getLine1() {
        return "Press the " + Keyboard.getKeyName(Settings.singleton.getPlayerListKey()) + " key to view";
    }

    @Override
    public String getLine2() {
        return "the player list.";
    }

    PlayerList playerList = new PlayerList();

    @Override
    public boolean isActive() {
        if (LegacyGameManager.isInGame() && Settings.singleton.getPlayerListToast())
            return playerList.hasPlayers();

        return false;
    }
}
