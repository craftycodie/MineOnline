package gg.codie.mineonline.gui.components.toast;

import gg.codie.mineonline.Settings;
import gg.codie.mineonline.client.LegacyGameManager;
import gg.codie.mineonline.gui.PlayerList;
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
