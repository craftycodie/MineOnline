package gg.codie.mineonline.protocol;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ListLevelsURLConnection extends HttpURLConnection {
    public ListLevelsURLConnection(URL url) {
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
        String[] worldNames = ClassicLevel.listLevels();

        return new ByteArrayInputStream(String.join(";", worldNames).getBytes());
    }
}
