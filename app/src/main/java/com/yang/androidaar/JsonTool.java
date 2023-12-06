package com.yang.androidaar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JsonTool {

//    private static Gson _gsonIns;
//
//    static {
//        _gsonIns = new Gson();
//    }

//    public static String toJson(Object obj) {
//        return _gsonIns.toJson(obj);
//    }

//    public static <T> T toObject(String json, Class<T> classOfT) {
//        try {
//            return _gsonIns.fromJson(json, classOfT);
//        } catch (Exception e) { // 不合法 json, 也返回实例
//            e.printStackTrace();
//            try {
//                return classOfT.newInstance();
//            } catch (Exception e1) {
//            }
//        }
//        return null;
//    }

//    public static String beauty(String txt) {
//        LinkedTreeMap map = _gsonIns.fromJson(txt, LinkedTreeMap.class);
//        return beauty(map);
//    }

//    public static String beauty(Object obj) {
//        try {
//            return new GsonBuilder().setPrettyPrinting().create().toJson(obj);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    public static String[] toStringArray(JSONArray ja) {
        List<String> strLst = new ArrayList<>();
        if (ja == null) return strLst.toArray(new String[0]);

        try {
            for (int i = 0; i < ja.length(); i++) {
                strLst.add(ja.getString(i));
            }
            return strLst.toArray(new String[0]);
        } catch (Exception e) {
            e.printStackTrace();
            return new String[]{};
        }
    }

    public static JSONArray fromStringArray(String[] strArr) {
        if (strArr == null) return new JSONArray();
        try {
            return new JSONArray(strArr);
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    // 只支持一级
    public static Map<String, Object> toMap(JSONObject jo) {
        Map<String, Object> map = new HashMap<>();
        if (jo == null) return map;

        try {
            Iterator<String> keys = jo.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                Object value = jo.get(key);
                if (value instanceof JSONArray) {
                    value = toList((JSONArray) value);
                } else if (value instanceof JSONObject) {
                    value = toMap((JSONObject) value);
                }
                map.put(key, value);
            }
            return new HashMap<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    public static List<Object> toList(JSONArray array) {
        try {
            List<Object> list = new ArrayList();
            for (int i = 0; i < array.length(); i++) {
                Object value = array.get(i);
                if (value instanceof JSONArray) {
                    value = toList((JSONArray) value);
                } else if (value instanceof JSONObject) {
                    value = toMap((JSONObject) value);
                }
                list.add(value);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList();
        }
    }

    public static JSONObject fromMap(Map<String, Object> m) {
        return new JSONObject(m);
    }
}
