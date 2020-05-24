package gg.codie.mineonline.gui.rendering;

import com.intellij.uiDesigner.core.GridConstraints;
import gg.codie.mineonline.MineOnlineLauncherFrame;
import gg.codie.mineonline.Properties;
import org.lwjgl.LWJGLException;

import javax.swing.*;
import java.awt.*;

public class SkinFormTest extends JFrame {
    private JPanel skinPanel;
    private JPanel contentPanel;

    public static void main(String[] args) throws LWJGLException {
        Properties.loadProperties();

        JFrame frame = new SkinFormTest();
        frame.setVisible(true);
    }

    public SkinFormTest() throws LWJGLException {

        super("Skin Viewer");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(contentPanel);
        setSize(600, 575);
        setResizable(false);

        PlayerModelCanvas canvas = new PlayerModelCanvas();

        skinPanel.add(canvas, new GridConstraints());

        //JFrame frame = new JFrame("AWTGLCanvas - multisampling");
        //frame.setPreferredSize(new Dimension(640, 480));
        //frame.add(skinPanel, new GridConstraints());
        //frame.pack();

        //frame.setVisible(true);
    }
}
