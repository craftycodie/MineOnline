package gg.codie.mineonline;

import gg.codie.mineonline.gui.rendering.PlayerRendererTest;

import java.awt.*;

public class MineOnline {
    public static void main(String[] args) throws Exception{
        LibraryManager.extractLibraries();
        LibraryManager.updateClasspath();
        LibraryManager.updateNativesPath();

        System.setProperty("http.proxyHost", "0.0.0.0");
        System.setProperty("http.proxyPort", "" + args[0]);

        PlayerRendererTest.main(null);
    }
}
