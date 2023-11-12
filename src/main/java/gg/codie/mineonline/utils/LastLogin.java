package gg.codie.mineonline.utils;

import gg.codie.mineonline.LauncherFiles;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

public class LastLogin {
    public final String accessToken;
    public final String clientToken;
    public final String loginUsername;
    public final String username;
    public final String uuid;
    public final boolean legacy;

    public static void writeLastLogin(String accessToken, String clientToken, String loginUsername, String username, String uuid, boolean legacy) {
        File lastLogin = new File(LauncherFiles.LAST_LOGIN_PATH);

        try (FileOutputStream fileOutputStream = new FileOutputStream(lastLogin)) {
            DataOutputStream dos;

            Cipher cipher = getCipher(1, "passwordfile");
            if (cipher != null) {
                dos = new DataOutputStream(new CipherOutputStream(fileOutputStream, cipher));
            } else {
                dos = new DataOutputStream(fileOutputStream);
            }
            dos.writeUTF(accessToken);
            dos.writeUTF(clientToken);
            dos.writeUTF(loginUsername);
            dos.writeUTF(username);
            dos.writeUTF(uuid);
            dos.writeBoolean(legacy);
            dos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteLastLogin() {
        try {
            Files.delete(Paths.get(LauncherFiles.LAST_LOGIN_PATH));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
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
        File lastLogin = new File(LauncherFiles.LAST_LOGIN_PATH);
        try (FileInputStream fileInputStream = new FileInputStream(lastLogin)) {
            DataInputStream dis;

            Cipher cipher = getCipher(2, "passwordfile");
            if (cipher != null) {
                dis = new DataInputStream(new CipherInputStream(fileInputStream, cipher));
            } else {
                dis = new DataInputStream(fileInputStream);
            }
            String accessToken = dis.readUTF();
            String clientToken = dis.readUTF();
            String loginUsername = dis.readUTF();
            String username = dis.readUTF();
            String uuid = dis.readUTF();
            boolean legacy = dis.readBoolean();
            if(accessToken.length() > 0 && username.length() > 0 && uuid.length() > 0) {
                return new LastLogin(accessToken, clientToken, loginUsername, username, uuid, legacy);
            }
            dis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private LastLogin(String accessToken, String clientToken, String loginUsername, String username, String uuid, boolean legacy) {
        this.accessToken = accessToken;
        this.clientToken = clientToken;
        this.loginUsername = loginUsername;
        this.username = username;
        this.uuid = uuid;
        this.legacy = legacy;
    }
}
