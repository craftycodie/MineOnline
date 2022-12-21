package gg.codie.mineonline.protocol;

import java.net.*;

public class MineOnlineURLStreamHandlerFactory implements URLStreamHandlerFactory {
    private final Class<? extends URLConnection> defaultHttpConnectionClass;

    public MineOnlineURLStreamHandlerFactory() {
        try {
            URL foo = new URL("http://example.com");
            // Doesn't actually establish a connection
            defaultHttpConnectionClass = foo.openConnection().getClass();
        } catch (Exception e) {
            // this should never happen as the URL is hardcoded, shouldn't be invalid.
            throw new RuntimeException(e);
        }
    }

    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        if ("http".equals(protocol)) {
            return new MineOnlineURLStreamHandler(defaultHttpConnectionClass);
        }

        return null;
    }
}
