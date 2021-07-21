package gg.codie.mineonline.protocol;

import gg.codie.mineonline.LauncherFiles;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ClassicLevel {
    public enum ClassicLevelVersion {
        V1,
        V2
    }

    public String mapName;
    public byte mapID;
    public byte[] mapData;
    public ClassicLevelVersion version;

    private ClassicLevel() {

    }

    public static ClassicLevel fromSaveRequest(InputStream inputStream) throws IOException {
        ClassicLevel classicLevel = new ClassicLevel();
        DataInputStream dis = new DataInputStream(inputStream);

        dis.readUTF(); // username
        dis.readUTF(); // sessionID
        classicLevel.mapName = dis.readUTF();
        classicLevel.mapID = dis.readByte();
        int mapLength = dis.readInt();
        classicLevel.mapData = new byte[mapLength];
        dis.read(classicLevel.mapData, 0, mapLength);
        if (classicLevel.mapData[0] == (byte)0x1F && classicLevel.mapData[1] == (byte)0x8B)
            classicLevel.version = ClassicLevelVersion.V2;
        else
            classicLevel.version = ClassicLevelVersion.V1;

        return classicLevel;
    }

    public static ClassicLevel fromFile(int mapID) throws IOException {
        String[] levels = listLevels();
        if (levels[mapID - 1].equals("-"))
            throw new FileNotFoundException("Level not found.");

        byte[] mapData = new byte[(int)Files.size(Paths.get(LauncherFiles.MINEONLINE_WORLDS_FOLDER + mapID + "_" + levels[mapID - 1] + ".mine"))];
        new FileInputStream(new File(LauncherFiles.MINEONLINE_WORLDS_FOLDER + mapID + "_" + levels[mapID - 1] + ".mine")).read(mapData, 0, mapData.length);

        ClassicLevel classicLevel = new ClassicLevel();
        classicLevel.mapID = (byte)mapID;
        classicLevel.mapName = levels[mapID - 1];
        classicLevel.mapData = mapData;
        if (classicLevel.mapData[0] == (byte)0x1F && classicLevel.mapData[1] == (byte)0x8B)
            classicLevel.version = ClassicLevelVersion.V2;
        else
            classicLevel.version = ClassicLevelVersion.V1;

        return classicLevel;
    }

    public static String[] listLevels() {
        String[] worldNames = new String[] {"-", "-", "-", "-", "-"};

        File dir = new File(LauncherFiles.MINEONLINE_WORLDS_FOLDER);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                if (child.getName().endsWith(".mine") && child.getName().toCharArray()[1] == '_') {
                    worldNames[Integer.parseInt("" + child.getName().toCharArray()[0]) - 1] = child.getName().substring(2, child.getName().length() - 5);
                }
            }
        }

        return worldNames;
    }

    public InputStream toLoadResponse() throws IOException {
        byte[] responseData = new byte[mapData.length + 4];
        responseData[0] = 0x00;
        responseData[1] = 0x02;
        responseData[2] = 0x6F;
        responseData[3] = 0x6B;
        System.arraycopy(mapData, 0, responseData, 4, mapData.length);
        return new ByteArrayInputStream(responseData);
    }

    public void saveToFile() throws IOException {
        File worldFile = new File(LauncherFiles.MINEONLINE_WORLDS_FOLDER + (mapID + 1) + "_" + mapName + ".mine");
        FileOutputStream fos = new FileOutputStream(worldFile);
        fos.write(mapData);
        fos.flush();
        fos.close();
    }
}
