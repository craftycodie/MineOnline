package gg.codie.mineonline.protocol;

import sun.net.www.protocol.http.HttpURLConnection;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class MineOnlineURLStreamHandler extends URLStreamHandler {
    @Override
    protected URLConnection openConnection(URL url) throws IOException {
        // Online-Mode fix
        if (url.toString().contains("/game/joinserver.jsp"))
            return new JoinServerURLConnection(url);
        // Old anti-piracy endpoints return positive responses.
        else if (url.toString().contains("/login/session.jsp")) // May be unused.
            return new BasicResponseURLConnection(url, 200, "ok");
        else if (url.toString().contains("login.minecraft.net"))
            return new BasicResponseURLConnection(url, 200, "ok");
        else if (url.toString().contains("/game/?n="))
            return new BasicResponseURLConnection(url, 200, "42069");
        else if (url.toString().contains("/haspaid.jsp"))
            return new BasicResponseURLConnection(url, 200, "true");
        // These move classic worlds to local files, as the level api is long gone.
        else if (url.toString().contains("level/save.html"))
            return new SaveLevelURLConnection(url);
        else if (url.toString().contains("level/load.html"))
            return new LoadLevelURLConnection(url);
        else if (url.toString().contains("listmaps.jsp"))
            return new ListLevelsURLConnection(url);
        // Sounds are downloaded by the launcher, so we spoof the index.
        else if (url.toString().endsWith("/MinecraftResources/"))
            return new ResourcesIndexURLConnection(url);
        // Fallback for #323
        else if (url.toString().contains("/MinecraftResources/"))
            return new ResourceDownloadURLConnection(url);
        else if (url.toString().endsWith("/resources/"))
            return new ClassicResourcesIndexURLConnection(url);

        // Skins are pulled from the new endpoint and converted to the legacy format as required.
        for (String oldSkinAddress : SkinURLConnection.OLD_SKIN_ADDRESSES) {
            if (url.toString().startsWith(oldSkinAddress))
                return new SkinURLConnection(url);
        }
        // Capes are pulled from the new endpoint, no conversion is required.
        for (String oldCapeAddress : CapeURLConnection.OLD_CAPE_ADDRESSES) {
            if (url.toString().startsWith(oldCapeAddress))
                return new CapeURLConnection(url);
        }

        return new HttpURLConnection(url, null);
    }
}
