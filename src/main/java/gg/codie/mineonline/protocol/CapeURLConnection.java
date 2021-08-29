package gg.codie.mineonline.protocol;

import gg.codie.mineonline.gui.textures.TextureHelper;
import gg.codie.mineonline.utils.SkinUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class CapeURLConnection extends HttpURLConnection {
    public CapeURLConnection(URL url) {
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
        String username = url.contains("/MinecraftCloaks/")
                ? url.substring(url.indexOf("/MinecraftCloaks/"))
                .replace("/MinecraftCloaks/", "")
                .replace(".png", "")
                : url.substring(url.indexOf("/cloak/get.jsp?user="))
                .replace("/cloak/get.jsp?user=", "");

        try {
            inputStream = SkinUtils.findCloakURLForUsername(username).openConnection().getInputStream();
        } catch (Exception ex) {
            responseCode = 404;
        }
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
