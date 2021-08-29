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

    @Override
    public void disconnect() {

    }

    @Override
    public boolean usingProxy() {
        return false;
    }

    InputStream inputStream = null;
    int responseCode = 200;

    @Override
    public void connect() throws IOException {
        String url = this.url.toString();
        String username = (url.contains("/MinecraftSkins/")
                ? url.substring(url.indexOf("/MinecraftSkins/"))
                .replace("/MinecraftSkins/", "")
                .replace(".png", "")
                : url.substring(url.indexOf("/skin/")))
                .replace("/skin/", "")
                .replace(".png", "");

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
