package gg.codie.utils;

import gg.codie.mineonline.gui.MinecraftInstall;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class JSONUtils {
    public static String[] getStringArray(JSONArray jsonArray) {
        Iterator<Object> iterator = jsonArray.iterator();

        LinkedList<String> strings = new LinkedList();

        while(iterator.hasNext()) {
            strings.add(iterator.next().toString());
        }

        return strings.toArray(new String[jsonArray.length()]);
    }

    public static List<MinecraftInstall> getMinecraftInstalls(JSONArray jsonArray) {
        Iterator<Object> iterator = jsonArray.iterator();

        LinkedList<MinecraftInstall> installs = new LinkedList();

        while(iterator.hasNext()) {
            JSONObject object = (JSONObject)iterator.next();
            installs.add(new MinecraftInstall(
                    object.getString("name"),
                    object.getString("mainClass"),
                    object.getString("appletClass"),
                    object.getString("jarPath")
            ));
        }

        return installs;
    }

    public static JSONArray setMineraftInstalls(List<MinecraftInstall> installs) {
        JSONArray array = new JSONArray();

        Iterator<MinecraftInstall> iterator = installs.iterator();

        while(iterator.hasNext()) {
            MinecraftInstall install = iterator.next();
            JSONObject object = new JSONObject();
            object.put("name", install.getName());
            object.put("mainClass", install.getMainClass());
            object.put("appletClass", install.getAppletClass());
            object.put("jarPath", install.getJarPath());
            array.put(object);
        }

        return array;
    }
}
