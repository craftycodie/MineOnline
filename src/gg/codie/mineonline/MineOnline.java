package gg.codie.mineonline;

import gg.codie.mineonline.gui.LoginForm;
import gg.codie.mineonline.gui.events.IOnClickListener;
import gg.codie.mineonline.gui.rendering.DisplayManager;
import gg.codie.mineonline.gui.rendering.PlayerRendererTest;
import org.lwjgl.opengl.Display;

import javax.swing.*;
import java.awt.*;

public class MineOnline {
    static LoginForm loginForm = null;
    private static Canvas glCanvas = new Canvas();

    public static void main(String[] args) throws Exception{
        LibraryManager.extractLibraries();
        LibraryManager.updateClasspath();
        LibraryManager.updateNativesPath();

        System.setProperty("http.proxyHost", "0.0.0.0");
        System.setProperty("http.proxyPort", "" + args[0]);

//        DisplayManager.init();
//
//        DisplayManager.createDisplay();


//        loginForm = new LoginForm(new IOnClickListener() {
//            @Override
//            public void onClick() {
//                try {
//                    try {
//                        Display.setParent(glCanvas);
//                    } catch (Exception e) {}
//                    loginForm.getContent().setVisible(false);
////                    DisplayManager.closeDisplay();
//                    DisplayManager.getCanvas().setVisible(true);
//                    DisplayManager.getFrame().setVisible(true);
//                    DisplayManager.getFrame().pack();
//                    //PlayerRendererTest.main(null);
//                } catch (Exception ex) {}
//            }
//        });
//        JPanel loginPanel = loginForm.getContent();
//        DisplayManager.getFrame().add(loginPanel);
//        //DisplayManager.getFrame().setContentPane(loginPanel);
//        loginPanel.setVisible(true);
//        loginPanel.setSize(DisplayManager.getDefaultWidth(), DisplayManager.getDefaultHeight());
//        DisplayManager.getFrame().setVisible(true);
//        DisplayManager.getFrame().pack();




        PlayerRendererTest.main(null);

////        DisplayManager.closeDisplay();
//        new Session("codie", "5ed2c7eed4c7ad8928b38f97");
//
//        new MinecraftLauncher("D:\\Projects\\Local\\MinecraftBetaOfflineLauncher\\Dev\\out\\artifacts\\Minecraft\\Minecraft.jar", null, null, null).startMinecraft();

        //new MinecraftLauncher("D:\\Projects\\GitHub\\MineOnline\\jars\\c0.0.11a-launcher.jar", null, null, null).startMinecraft();

    }

}
