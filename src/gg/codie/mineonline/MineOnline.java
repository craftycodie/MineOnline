package gg.codie.mineonline;

import gg.codie.mineonline.gui.MenuManager;
import gg.codie.mineonline.patches.LWJGLDisplayPatch;

public class MineOnline {
    public static void main(String[] args) throws Exception{
        LibraryManager.updateNativesPath();

        LWJGLDisplayPatch.hijackLWJGLThreadPatch();

        MenuManager.main(args);
    }
}
