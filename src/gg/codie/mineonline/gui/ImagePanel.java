package gg.codie.mineonline.gui;

import javax.swing.*;
import java.awt.*;

public class ImagePanel extends JPanel {

    private String path;
    private ImageIcon imageIcon = new ImageIcon(this.path);
    private Image image = imageIcon.getImage();

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
        imageIcon = new ImageIcon(this.path);
        image = imageIcon.getImage();
    }

    private void createUIComponents() {
        imageIcon = new ImageIcon(this.path);
        image = imageIcon.getImage();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (image != null) g.drawImage(image,0,0,getWidth(),getHeight(),this);
    }
}