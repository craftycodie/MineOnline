package gg.codie.mineonline;

import gg.codie.mineonline.gui.MenuManager;

public class MineOnline {
    public static void main(String[] args) throws Exception{
        LibraryManager.extractLibraries();
        LibraryManager.updateClasspath();
        LibraryManager.updateNativesPath();

        URLPatch.redefineURL();

        System.setProperty("http.proxyHost", "0.0.0.0");
        System.setProperty("http.proxyPort", "" + args[0]);

        MenuManager.main(null);
    }
}
