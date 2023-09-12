package com.ahnewark.common.utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.LinkedList;

public class JSONUtils {
    public static String[] getStringArray(JSONArray jsonArray) {
        Iterator<Object> iterator = jsonArray.iterator();

        LinkedList<String> strings = new LinkedList();

        while(iterator.hasNext()) {
            strings.add(iterator.next().toString());
        }

        return strings.toArray(new String[jsonArray.length()]);
    }

    public static <T> T fromJSONFile(File jsonFile) throws FileNotFoundException {
        JSONTokener tokener = new JSONTokener(new FileInputStream(jsonFile));
        JSONObject object = new JSONObject(tokener);
        //Object
        return null;
    }


}
