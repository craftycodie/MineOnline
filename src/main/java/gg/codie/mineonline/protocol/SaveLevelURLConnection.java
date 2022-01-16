package gg.codie.mineonline.protocol;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SaveLevelURLConnection extends HttpURLConnection {
    public ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    public SaveLevelURLConnection(URL url) {
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
    public ByteArrayOutputStream getOutputStream() {
        return outputStream;
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
        ClassicLevel classicLevel = ClassicLevel.fromSaveRequest(new ByteArrayInputStream(outputStream.toByteArray()));
        classicLevel.saveToFile();

        return new ByteArrayInputStream("ok".getBytes());
    }
}
