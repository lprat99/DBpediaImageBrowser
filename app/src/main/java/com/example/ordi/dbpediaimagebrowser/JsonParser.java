package com.example.ordi.dbpediaimagebrowser;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ordi on 31/01/2016.
 */
public class JsonParser {
    public String gettitle(String obj) {
        String rm = null;
        try {


            JSONObject reader = new JSONObject(obj);
            rm = String.valueOf(reader.get("query"));
            reader = new JSONObject(rm);
            JSONArray ar = reader.getJSONArray("search");
            JSONObject rec = ar.getJSONObject(0);
            rm= rec.getString("title");


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rm;
    }

    public String suggestion(String obj) {
        String rm = null;
        try {


            JSONObject reader = new JSONObject(obj);
            rm = String.valueOf(reader.get("query"));
            reader = new JSONObject(rm);
            rm= String.valueOf( reader.get("searchinfo"));
            reader = new JSONObject(rm);
            try {
                rm=reader.getString("suggestion");
            }catch (Exception e){
                rm="nosuggestion";
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rm;
    }

    public String getdbpediaimage(String obj){
        try {
            JSONObject reader = new JSONObject(obj);
            reader = new JSONObject(reader.get("results").toString());
            JSONArray ar = reader.getJSONArray("bindings");
            reader = new JSONObject(ar.getJSONObject(0).get("name").toString());
            return reader.get("value").toString();
        }catch (Exception e){
            Log.d("sparqlerror", e.toString());
            return null;
        }


    }
}
