package gg.codie.mineonline.gui.rendering;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.AWTGLCanvas;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

import static org.lwjgl.opengl.GL11.*;

public class CanvasTest {

    public static AWTGLCanvas canvas;

    public static void main (String[] args) {

        final JPanel panel_canvas = new JPanel ();

        try {
            panel_canvas.add (canvas = new AWTGLCanvas (new PixelFormat (8, 8, 0, 0)) {

                final Dimension size = new Dimension (100, 100);

                @Override
                public void initGL () {

                    setPreferredSize (size);
                    setSize          (size);

                    glDisable (GL_DEPTH_TEST);
                    glDisable (GL_CULL_FACE);
                }

                @Override
                public void paintGL () {

                    try {

                        glViewport (10, 0, getWidth (), getHeight ());
                        float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
                        glFrustum(-0.01, 0.01, -0.01 / aspectRatio, 0.01 / aspectRatio, 0.01, 100000);

                        glColor3f (1,0,0);

                        glBegin (GL_QUADS);

                        glEnd ();

                        glFinish ();

                        swapBuffers ();

                    } catch (LWJGLException ex) {}
                }

            }, BorderLayout.CENTER);

        } catch (Exception e) {};

        JPanel panel_top      = new JPanel (new FlowLayout ());
        JPanel panel_bottom   = new JPanel (new BorderLayout ()) {

            @Override
            public void paint (Graphics g) {

                canvas.update (g);
            }
        };

        panel_top   .setPreferredSize (new Dimension (200, 300));
        panel_bottom.setPreferredSize (new Dimension (200, 300));

        panel_bottom.add (panel_canvas);

        JScrollPane scrollpane_top    = new JScrollPane (panel_top);
        JScrollPane scrollpane_bottom = new JScrollPane (panel_bottom);

        JSplitPane splitpane = new JSplitPane (JSplitPane.VERTICAL_SPLIT);

        splitpane.setPreferredSize (new Dimension (200, 300));

        splitpane.setTopComponent       (scrollpane_top);
        splitpane.setBottomComponent    (scrollpane_bottom);

        splitpane.setContinuousLayout (true);
        splitpane.setResizeWeight (0.5f);

        JFrame frame = new JFrame ();

        frame.add (splitpane);
        frame.pack ();

        frame.setVisible (true);
    }
}