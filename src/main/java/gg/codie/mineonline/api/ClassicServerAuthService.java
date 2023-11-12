package gg.codie.mineonline.api;

import gg.codie.common.utils.SHA1Utils;
import gg.codie.minecraft.api.SessionServer;
import gg.codie.mineonline.Globals;

import java.net.InetAddress;
import java.util.LinkedList;

public class ClassicServerAuthService {
    private LinkedList<IMPPassProvider> mpPassProviders = new LinkedList<>();

    public ClassicServerAuthService() {
        registerMPPassProviders();
    }

    private void registerMPPassProviders() {
        mpPassProviders.add(new BetaCraftMPPassProvider());
    }

    public String getMPPass(String serverIP, String serverPort, String accessToken, String userID, String username) {
        try {
            InetAddress inetAddress = InetAddress.getByName(serverIP);
            serverIP = inetAddress.getHostAddress();

            if (inetAddress.isAnyLocalAddress()) {
                // TODO: reimplement.
//                serverIP = MineOnlineAPI.getExternalIP();
            }
        } catch (Exception ex) {
            //ignore.
        }

        try {
            if (!SessionServer.joinGame(
                    accessToken,
                    userID,
                    SHA1Utils.sha1(serverIP + ":" + serverPort)
            )) {
                if (Globals.DEV) {
                    System.out.println("Bad server join.");
                }
                System.out.println("Bad session.");
                return "0";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return "0";
        }

        for (IMPPassProvider mpPassProvider : mpPassProviders) {
            String mppass = mpPassProvider.getMPPass(serverIP, serverPort, username);
            if (mppass != null) {
                if (Globals.DEV)
                    System.out.println("Got MPPass " + mppass + " from " + mpPassProvider.getClass().toString());
                return mppass;
            }
        }
        return "0";
    }
}
