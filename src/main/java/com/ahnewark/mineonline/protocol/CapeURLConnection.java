package com.ahnewark.mineonline.protocol;

import com.ahnewark.mineonline.utils.SkinUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class CapeURLConnection extends HttpURLConnection {
    public CapeURLConnection(URL url) {
        super(url);
    }

    public final static String[] OLD_CAPE_ADDRESSES = new String[] {
            "http://www.minecraft.net/cloak/get.jsp?user=", // Introduced Beta 1.0 (when capes were added)
            "http://s3.amazonaws.com/MinecraftCloaks/",     // Introduced Beta 1.2
            "http://skins.minecraft.net/MinecraftCloaks/"   // Introduced Release 1.3.1
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
        for (String oldCapeAddress : OLD_CAPE_ADDRESSES) {
            username = username.replace(oldCapeAddress, "");
        }
        /// ... and dropping the .png.
        username = username.replace(".png", "");

        return username;
    }

    @Override
    public void connect() throws IOException {
        String username = getUsernameFromURL();

        URL capeUrl = SkinUtils.findCloakURLForUsername(username);
        if (capeUrl == null) {
            capeUrl = SkinUtils.findEventCloakURLForUsername(username);
        }
        if (capeUrl == null) {
            responseCode = 404;
        }
        else {
            inputStream = capeUrl.openConnection().getInputStream();
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
