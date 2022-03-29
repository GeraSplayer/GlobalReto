package com.example.global.network;

import android.content.Context;
import android.os.AsyncTask;

import com.example.global.classes.Item;
import com.example.global.interfaces.NetworkCallback;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Networking extends AsyncTask<Object, Integer, Object> {

    static final String SERVER_PATH = "https://api.yelp.com/v3/";
    static final int TIMEOUT = 3000;
    static final String API_KEY = "Bearer VHU5423PonyHZZhsWAJEpjSK1WWlTH7Jv8tAk18vDtcUWCG6qKbIH4llHum7tUdUE1Pyz2IKWmVkoRg4rzdiJsJJQYOuR_Jv-eAmKK2Y7HDGRN_0ccjBfv27Gd8_YnYx";
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();

    Context m_context;

    public Networking(Context m_context) {
        this.m_context = m_context;
    }

    @Override
    protected Object doInBackground(Object... objects) {
        String action = (String) objects[0];
        NetworkCallback networkCallback = (NetworkCallback) objects[1];
        if(action.equals("search")){
            List<Item> itemList = searching((String)objects[2], (double)objects[3], (double)objects[4]);
            networkCallback.onWorkFinish(itemList);
        }else if(action.equals("detalle")){
            Item item = getById( (String) objects[2] );
            networkCallback.onWorkFinish(item);
        }else if(action.equals("autocomplete")){
            ArrayList<String> autoList = autocompleteList((String)objects[2], (double)objects[3], (double)objects[4]);
            networkCallback.onWorkFinish(autoList);
        }
        return null;
    }

    private List<Item> searching(String term, double latitud, double longitud){
        List<Item> itemList = new ArrayList<>();
        try {
            String response = post(SERVER_PATH+"businesses/search?term="+term+"&latitude="+latitud+"&longitude="+longitud, API_KEY);
            Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().serializeNulls().create();
            JSONObject jsonObject = new JSONObject( gson.fromJson(response, JsonObject.class).toString() );

            JSONArray jsonArray = jsonObject.optJSONArray("businesses");
            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject obj = (JSONObject) jsonArray.get(j);
                itemList.add(new Item(obj));
            }
            if(jsonArray.length() <=0 )
                itemList = null;

            }catch (IOException | JSONException ioException){
            ioException.printStackTrace();
        }

        return itemList;
    }
    private Item getById(String id){
        Item i = null;
        try {
            String response = post(SERVER_PATH+"businesses/"+id, API_KEY);
            Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().serializeNulls().create();
            JSONObject obj = new JSONObject( gson.fromJson(response, JsonObject.class).toString() );
            i = new Item(obj);
        }catch (IOException | JSONException ioException){
            ioException.printStackTrace();
        }

        return i;
    }
    private ArrayList<String> autocompleteList(String term, double latitud, double longitud){
        ArrayList<String> autoList = new ArrayList<>();
        try {
            String response = post(SERVER_PATH+"autocomplete?text="+term+"&latitude="+latitud+"&longitude="+longitud, API_KEY);
            Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().serializeNulls().create();
            JSONObject jsonObject = new JSONObject( gson.fromJson(response, JsonObject.class).toString() );

            JSONArray businessesArray = jsonObject.optJSONArray("businesses");
            if(businessesArray != null) {
                for (int j = 0; j < businessesArray.length(); j++) {
                    JSONObject obj = (JSONObject) businessesArray.get(j);
                    autoList.add(obj.optString("name", ""));
                }
            }
            JSONArray categoriesArray = jsonObject.optJSONArray("categories");
            if(categoriesArray != null) {
                for (int j = 0; j < categoriesArray.length(); j++) {
                    JSONObject obj = (JSONObject) categoriesArray.get(j);
                    autoList.add(obj.optString("title", ""));
                }
            }
            JSONArray termsArray = jsonObject.optJSONArray("terms");
            if(termsArray !=null) {
                for (int j = 0; j < termsArray.length(); j++) {
                    JSONObject obj = (JSONObject) termsArray.get(j);
                    autoList.add(obj.optString("text", ""));
                }
            }

        }catch (IOException | JSONException |NullPointerException ioException){
            ioException.printStackTrace();
        }

        return autoList;
    }

    String post(String url, String header) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", header)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

}
