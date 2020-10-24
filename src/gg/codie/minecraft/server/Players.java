package gg.codie.minecraft.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

public class Players {
    public static String[] readClassicPlayersFile(String path) {
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