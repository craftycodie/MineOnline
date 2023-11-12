package gg.codie.mineonline.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoadLevelURLConnection extends HttpURLConnection {
    public LoadLevelURLConnection(URL url) {
        super(url);
    }

    @Override
    public void disconnect() {

    }

    @Override
    public boolean usingProxy() {
        return false;
    }

    @Override
    public void connect() throws IOException {

    }

    @Override
    public int getResponseCode() throws IOException {
        return 200;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        int mapID = url.toString().charAt(url.toString().indexOf("id=") + 3) - 47;
        ClassicLevel classicLevel = ClassicLevel.fromFile(mapID);

        return classicLevel.toLoadResponse();
    }
}
