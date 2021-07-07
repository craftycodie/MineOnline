package gg.codie.mineonline.levels;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

public class MineOnlineURLStreamHandlerFactory implements URLStreamHandlerFactory {
    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        if ("mineonline".equals(protocol)) {
            return new MineOnlineURLStreamHandler();
        }

        return null;
    }
}
