package gg.codie.mineonline.gui.textures;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class TextureHelper {
    // Used in reflection.
    public static InputStream convertModernSkin(InputStream inputStream){
        try {
            BufferedImage skin = ImageIO.read(inputStream);
            BufferedImage croppedSkin = skin.getSubimage(0, 0, 64, 32);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(croppedSkin, "png", os);
            byte[] bytes = os.toByteArray();
            return new ByteArrayInputStream(bytes);
        } catch (Exception ex) {
            return null;
        }
    }
}
