package gg.codie.minecraft.client;

import java.io.*;
import java.util.LinkedList;

public class Options {
    String path;

    public Options(String path) throws IOException {
        if(!new File(path).exists()) {
            new File(path).createNewFile();
        }
        this.path = path;
    }

    public void setOption(String name, String value) throws IOException {
        LinkedList<String> lines = new LinkedList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path, false))) {
            boolean foundExisting = false;
            for (String line : lines) {
                if (line.startsWith(name + ":")) {
                    line = name + ":" + value;
                    foundExisting = true;
                }
                bw.write(line);
                bw.newLine();
            }
            if(!foundExisting) {
                bw.write(name + ":" + value);
                bw.newLine();
            }
        }
    }

    public String getOption(String name) throws NoSuchFieldException, IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                if(line.startsWith(name + ":")) {
                    return line.replace(name + ":", "");
                }
            }
        }
        throw new NoSuchFieldException(name + " not found.");
    }
}
