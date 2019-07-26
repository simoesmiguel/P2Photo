package jsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class JsonParser {


    public static HashMap<String,String> jsonToMap(String t) throws JSONException {

        JSONObject jObject = new JSONObject(t);
        HashMap<String,String> map= iterateOverObject(jObject);

        return map;
    }

    

    public static List<Object> jsonArrayToMap(String t) throws JSONException {

        JSONArray jArray = new JSONArray(t);
        ArrayList<Object> list = iterateOverArray(jArray);

        return list;
    }



    private static ArrayList<Object> iterateOverArray(JSONArray jsonArray) throws JSONException {
        ArrayList<Object> list = new ArrayList();
        for (int i = 0; i < jsonArray.length(); i++) {
            Object o = jsonArray.get(i);
            if (o instanceof JSONObject){
                list.add(iterateOverObject((JSONObject) o));
            }else if (o instanceof JSONArray){
                list.add(iterateOverArray((JSONArray) o));
            }else{
                throw new JSONException("ERROR CLASS NOT UNDERSTOOD: " + o.getClass().getName());
            }
        }
        return list;
    }

    private static HashMap<String,String> iterateOverObject(JSONObject jsonObject) throws JSONException {
        HashMap<String, String> map = new HashMap<String, String>();
        Iterator<?> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            String value = jsonObject.getString(key);
            map.put(key, value);
        }

        return map;
    }
}