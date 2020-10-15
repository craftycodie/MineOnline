package gg.codie.mineonline.utils;

import gg.codie.mineonline.LauncherFiles;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.io.*;
import java.util.Random;

public class LastLogin {
    public final String accessToken;
    public final String clientToken;
    public final String loginUsername;
    public final String username;
    public final String uuid;

    public static void writeLastLogin(String accessToken, String clientToken, String loginUsername, String username, String uuid) {
        try {
            DataOutputStream dos;
            File lastLogin = new File(LauncherFiles.LAST_LOGIN_PATH);

            Cipher cipher = getCipher(1, "passwordfile");
            if (cipher != null) {
                dos = new DataOutputStream(new CipherOutputStream(new FileOutputStream(lastLogin), cipher));
            } else {
                dos = new DataOutputStream(new FileOutputStream(lastLogin));
            }
            dos.writeUTF(accessToken);
            dos.writeUTF(clientToken);
            dos.writeUTF(username);
            dos.writeUTF(uuid);
            dos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteLastLogin() {
        File lastLogin = new File(LauncherFiles.LAST_LOGIN_PATH);
        lastLogin.delete();
    }

    private static Cipher getCipher(int mode, String password) throws Exception {
        Random random = new Random(43287234L);
        byte[] salt = new byte[8];
        random.nextBytes(salt);
        PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, 5);

        SecretKey pbeKey = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(new PBEKeySpec(password.toCharArray()));
        Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
        cipher.init(mode, pbeKey, pbeParamSpec);
        return cipher;
    }

    public static LastLogin readLastLogin() {
        try {
            DataInputStream dis;
            File lastLogin = new File(LauncherFiles.LAST_LOGIN_PATH);

            Cipher cipher = getCipher(2, "passwordfile");
            if (cipher != null) {
                dis = new DataInputStream(new CipherInputStream(new FileInputStream(lastLogin), cipher));
            } else {
                dis = new DataInputStream(new FileInputStream(lastLogin));
            }
            String accessToken = dis.readUTF();
            String clientToken = dis.readUTF();
            String loginUsername = dis.readUTF();
            String username = dis.readUTF();
            String uuid = dis.readUTF();
            if(accessToken.length() > 0 && clientToken.length() > 0 && username.length() > 0 && uuid.length() > 0 && loginUsername.length() > 0) {
                return new LastLogin(accessToken, clientToken, loginUsername, username, uuid);
            }
            dis.close();
        } catch (Exception e) {
        }

        return null;
    }

    private LastLogin(String accessToken, String clientToken, String loginUsername, String username, String uuid) {
        this.accessToken = accessToken;
        this.clientToken = clientToken;
        this.loginUsername = loginUsername;
        this.username = username;
        this.uuid = uuid;
    }
}
