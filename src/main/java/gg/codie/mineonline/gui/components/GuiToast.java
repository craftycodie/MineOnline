package gg.codie.mineonline.gui.components;

import gg.codie.mineonline.client.LegacyGameManager;
import gg.codie.mineonline.gui.GUIScale;
import gg.codie.mineonline.gui.rendering.Font;
import gg.codie.mineonline.gui.rendering.Loader;
import gg.codie.mineonline.gui.rendering.Renderer;
import gg.codie.mineonline.gui.textures.EGUITexture;
import gg.codie.mineonline.gui.components.toast.*;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Arrays;

public class GuiToast extends GuiComponent
{
    private final ArrayList<IToast> toasts = new ArrayList<>(Arrays.asList(new IToast[] {
            new MenuToast(),
            new ScreenshotClipboardToast(),
            new ZoomToast(),
            new PlayerListToast(),
            new DeviceCodeClipboardToast(),
    }));

    public boolean isShowingToast() {
        return displayToast != null;
    }

    IToast displayToast;

    private void updateToastScale()
    {
        GL11.glViewport(0, 0, Display.getParent().getWidth(), Display.getParent().getHeight());
        GL11.glMatrixMode(5889 /*GL_PROJECTION*/);
        GL11.glLoadIdentity();
        GL11.glMatrixMode(5888 /*GL_MODELVIEW0_ARB*/);
        GL11.glLoadIdentity();
        toastWindowWidth = GUIScale.lastScaledWidth();
        toastWindowHeight = GUIScale.lastScaledHeight();
        GL11.glClear(256);
        GL11.glMatrixMode(5889 /*GL_PROJECTION*/);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0D, toastWindowWidth, toastWindowHeight, 0.0D, 1000D, 3000D);
        GL11.glMatrixMode(5888 /*GL_MODELVIEW0_ARB*/);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0F, 0.0F, -2000F);
    }

    public void renderToast()
    {
        if (!Mouse.isGrabbed() && LegacyGameManager.isInGame())
            return;

        if (displayToast != null && !displayToast.isActive() && System.currentTimeMillis() - toastTime > 3000)
            displayToast = null;

        if(displayToast == null) {
            for (IToast toast : toasts) {
                if (toast.isActive()) {
                    displayToast = toast;
                    toastTime = System.currentTimeMillis();
                    break;
                }
            }
        }

        if (displayToast == null)
            return;

        if (System.currentTimeMillis() - toastTime > 1500 && displayToast.isActive())
            toastTime = System.currentTimeMillis() - 1500;

        double d = (double)(System.currentTimeMillis() - toastTime) / 3000D;
        if((d < 0.0D || d > 1.0D))
        {
            toastTime = 0L;
            return;
        }
        updateToastScale();
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        double heightAnim = d * 2D;
        if(heightAnim > 1.0D)
        {
            heightAnim = 2D - heightAnim;
        }
        heightAnim *= 4D;
        heightAnim = 1.0D - heightAnim;
        if(heightAnim < 0.0D)
        {
            heightAnim = 0.0D;
        }
        heightAnim *= heightAnim;
        heightAnim *= heightAnim;
        int toastX = toastWindowWidth - 160;
        int toastY = 0 - (int)(heightAnim * 36D);
        int k = Loader.singleton.getGuiTexture(EGUITexture.TOAST);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, k);
        GL11.glDisable(GL11.GL_LIGHTING);
        Renderer.singleton.drawSprite(toastX, toastY, 96, 202, 160, 32);
        Font.minecraftFont.drawCenteredString(displayToast.getLine1(), toastX + 80, toastY + 7, 0x000000);
        Font.minecraftFont.drawCenteredString(displayToast.getLine2(), toastX + 80, toastY + 18, 0x000000);
        GL11.glPushMatrix();
        GL11.glRotatef(180F, 1.0F, 0.0F, 0.0F);
        GL11.glPopMatrix();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(32826 /*GL_RESCALE_NORMAL_EXT*/);
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    private int toastWindowWidth;
    private int toastWindowHeight;
    private long toastTime;

    @Override
    public void resize(int x, int y) {

    }
}
