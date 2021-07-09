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
        if (url.toString().contains("/game/checkserver.jsp"))
            return new CheckServerURLConnection(url);
        // Old anti-piracy endpoints return positive responses.
        else if (url.toString().contains("/login/session.jsp")) // May be unused.
            return new BasicResponseURLConnection(url, 200, "ok");
        else if (url.toString().contains("login.minecraft.net/session?name="))
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
        // Sounds are downloaded by the launcher, so we 404 the new index and spoof the old one.
        else if (url.toString().endsWith("/MinecraftResources/"))
            return new BasicResponseURLConnection(url, 404, "Not Found");
        else if (url.toString().endsWith("/resources/"))
            return new ClassicResourcesIndexURLConnection(url);
        // Skins are pulled from the new endpoint and converted to the legacy format as required.
        else if (url.toString().contains("/MinecraftSkins/") || url.toString().contains("/skin/"))
            return new SkinURLConnection(url);
        // Capes are pulled from the new endpoint.
        else if ((url.toString().contains("/MinecraftCloaks/") && url.toString().contains(".png")) || url.toString().contains("/cloak/get.jsp?user="))
            return new CapeURLConnection(url);
        else
            return new HttpURLConnection(url, null);
    }
}
