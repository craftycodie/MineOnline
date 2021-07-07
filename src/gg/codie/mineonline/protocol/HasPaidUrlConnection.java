package gg.codie.mineonline.protocol;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class HasPaidUrlConnection extends HttpURLConnection {
    public HasPaidUrlConnection(URL url) {
        super(url);
    }

    @Override
    public void connect() throws IOException {

    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream("true".getBytes());
    }

    @Override
    public void disconnect() {

    }

    @Override
    public boolean usingProxy() {
        return false;
    }
}
