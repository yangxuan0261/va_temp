package com.example.testapp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JsonTool {

    private static Gson _gsonIns;

    static {
        _gsonIns = new Gson();
    }

    public static String toJson(Object obj) {
        return _gsonIns.toJson(obj);
    }

    public static <T> T toObject(String json, Class<T> classOfT) {
        try {
            return _gsonIns.fromJson(json, classOfT);
        } catch (Exception e) { // 不合法 json, 也返回实例
            e.printStackTrace();
            try {
                return classOfT.newInstance();
            } catch (Exception e1) {
            }
        }
        return null;
    }

    public static String beauty(String txt) {
        LinkedTreeMap map = _gsonIns.fromJson(txt, LinkedTreeMap.class);
        return beauty(map);
    }

    public static String beauty(Object obj) {
        try {
            return new GsonBuilder().setPrettyPrinting().create().toJson(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
