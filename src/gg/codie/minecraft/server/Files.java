package gg.codie.minecraft.server;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

public class Files {
    public static String[][] readPlayersJSON(String path) {
        try {
            File usersFile = new File(path);
            if (usersFile.exists()) {
                LinkedList list = new LinkedList();
                BufferedReader reader = new BufferedReader(new FileReader(usersFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    list.add(line);
                }
                reader.close();
                LinkedList uuids = new LinkedList();
                LinkedList names = new LinkedList();
                for (Object jsonObject : new JSONArray(String.join("", (String[])list.toArray(new String[0])))) {
                    if (((JSONObject) jsonObject).has("uuid")) {
                        uuids.add(((JSONObject)jsonObject).getString("uuid"));
                    }

                    if (((JSONObject) jsonObject).has("name")) {
                        names.add(((JSONObject)jsonObject).getString("name"));
                    }
                }
                return new String[][] {
                        ((String[]) uuids.toArray(new String[0])),
                        ((String[]) names.toArray(new String[0]))
                };
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return new String[][] {new String[0], new String[0]};
    }

    public static String[] readBannedIPsJSON(String path) {
        try {
            File usersFile = new File(path);
            if (usersFile.exists()) {
                LinkedList list = new LinkedList();
                BufferedReader reader = new BufferedReader(new FileReader(usersFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    list.add(line);
                }
                reader.close();
                LinkedList ips = new LinkedList();
                for (Object jsonObject : new JSONArray(String.join("", (String[])list.toArray(new String[0])))) {
                    if (((JSONObject) jsonObject).has("ip")) {
                        ips.add(((JSONObject)jsonObject).getString("ip"));
                    }
                }
                return (String[])ips.toArray(new String[0]);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return new String[0];
    }

    public static String[] readUsersFile(String path) {
        try {
            File usersFile = new File(path);
            if (usersFile.exists()) {
                LinkedList list = new LinkedList();
                BufferedReader reader = new BufferedReader(new FileReader(usersFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    list.add(line);
                }
                reader.close();

                return (String[])list.toArray(new String[0]);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return new String[0];
    }
}
