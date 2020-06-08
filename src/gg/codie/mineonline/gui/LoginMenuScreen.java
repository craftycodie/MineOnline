package gg.codie.mineonline.gui;

import gg.codie.mineonline.Properties;
import gg.codie.mineonline.gui.events.IOnClickListener;
import gg.codie.mineonline.gui.rendering.*;
import gg.codie.mineonline.gui.rendering.Renderer;
import gg.codie.mineonline.gui.rendering.components.LargeButton;
import gg.codie.mineonline.gui.rendering.models.RawModel;
import gg.codie.mineonline.gui.rendering.models.TexturedModel;
import gg.codie.mineonline.gui.rendering.shaders.GUIShader;
import gg.codie.mineonline.gui.rendering.textures.ModelTexture;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.util.BufferedImageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class LoginMenuScreen implements IMenuScreen {
    LoginForm2 loginForm2;
    public LoginMenuScreen() {
        this.loginForm2 = new LoginForm2(DisplayManager.getFrame());
        //PopupMenu popupMenu = new PopupMenu();
        DisplayManager.getFrame().add(loginForm2);
        loginForm2.setSize(DisplayManager.getDefaultWidth(), DisplayManager.getDefaultHeight());
        //
    }

    public void update() {

    }

    public void render(Renderer renderer) {
//        int w = Display.getWidth();
//        int h = Display.getHeight();
//        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
//        Graphics2D g = bi.createGraphics();
//        loginForm2.paint(g);
        try {
//            Texture texture = BufferedImageUtil.getTexture("", bi);
//
//            GL11.glLoadIdentity();
//
//            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
//            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
//
//            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getTextureID());
//            GL11.glBegin(GL11.GL_QUADS);
//            GL11.glTexCoord2f(0, 0);
//            GL11.glVertex2i(0, 0);
//
//            GL11.glTexCoord2f(1, 0);
//            GL11.glVertex2i(512, 0);
//
//            GL11.glTexCoord2f(1, 1);;
//            GL11.glVertex2i(512, 512);
//
//            GL11.glTexCoord2f(0, 1);
//            GL11.glVertex2i(0, 512);
//            GL11.glEnd();
        } catch (Exception e) {

        }

    }

    public boolean showPlayer() {
        return false;
    }

    public void resize() {

    }

    public void cleanUp() {

    }
}
