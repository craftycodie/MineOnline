package gg.codie.mineonline.levels;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class ListLevelsURLConnection extends URLConnection {
    public ListLevelsURLConnection(URL url) {
        super(url);
    }

    @Override
    public void connect() throws IOException {

    }

    @Override
    public InputStream getInputStream() throws IOException {
        String[] worldNames = ClassicLevel.listLevels();

        return new ByteArrayInputStream(String.join(";", worldNames).getBytes());
    }


}
