package gg.codie.mineonline.levels;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class MineOnlineURLStreamHandler extends URLStreamHandler {
    @Override
    protected URLConnection openConnection(URL url) throws IOException {
        if (url.toString().contains("level/save.html"))
            return new SaveLevelURLConnection(url);
        if (url.toString().contains("level/load.html"))
            return new LoadLevelURLConnection(url);
        else if (url.toString().contains("listmaps.jsp"))
            return new ListLevelsURLConnection(url);
        else
            return null;
    }
}
