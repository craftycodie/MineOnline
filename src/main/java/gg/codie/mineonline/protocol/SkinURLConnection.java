package gg.codie.mineonline.protocol;

import gg.codie.mineonline.gui.textures.TextureHelper;
import gg.codie.mineonline.utils.SkinUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;

public class SkinURLConnection extends HttpURLConnection {
    public SkinURLConnection(URL url) {
        super(url);
    }

    public final static String[] OLD_SKIN_ADDRESSES = new String[] {
            "http://www.minecraft.net/skin/",               // Introduced Classic 0.0.18a (when skins were added)
            "http://s3.amazonaws.com/MinecraftSkins/",      // Introduced Beta 1.2
            "http://skins.minecraft.net/MinecraftSkins/"    // Introduced Release 1.3.1
    };

    @Override
    public void disconnect() {

    }

    @Override
    public boolean usingProxy() {
        return false;
    }

    InputStream inputStream = null;
    int responseCode = 200;

    private String getUsernameFromURL() {
        String username = this.url.toString();

        // We get the username from the skin by replacing the url up to the username with whitespace.
        for (String oldSkinAddress : OLD_SKIN_ADDRESSES) {
            username = username.replace(oldSkinAddress, "");
        }
        /// ... and dropping the .png.
        username = username.replace(".png", "");

        return username;
    }

    @Override
    public void connect() throws IOException {
        String username = getUsernameFromURL();

        JSONObject skin = SkinUtils.getUserSkin(username);
        TextureHelper.convertModernSkin(skin);

        if(skin == null) {
            responseCode = 404;
            return;
        }

        inputStream = TextureHelper.convertModernSkin(skin);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return inputStream;
    }

    @Override
    public int getResponseCode() {
        return responseCode;
    }
}
