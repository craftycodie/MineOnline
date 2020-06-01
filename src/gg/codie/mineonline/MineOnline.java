package gg.codie.mineonline;

import gg.codie.mineonline.gui.rendering.DisplayManager;
import gg.codie.mineonline.gui.rendering.PlayerRendererTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Map;

public class MineOnline {
    public static void main(String[] args) throws Exception{
        LibraryManager.extractLibraries();
        LibraryManager.updateClasspath();
        LibraryManager.updateNativesPath();

        System.setProperty("http.proxyHost", "0.0.0.0");
        System.setProperty("http.proxyPort", "" + args[0]);

        DisplayManager.init();

        PlayerRendererTest.main(null);

//        DisplayManager.closeDisplay();
        new Session("codie", "5ed2c7eed4c7ad8928b38f97");

        new MinecraftLauncher("D:\\Projects\\Local\\MinecraftBetaOfflineLauncher\\Dev\\out\\artifacts\\Minecraft\\Minecraft.jar", null, null, null).startMinecraft();

        //new MinecraftLauncher("D:\\Projects\\GitHub\\MineOnline\\jars\\c0.0.11a-launcher.jar", null, null, null).startMinecraft();

    }

}
