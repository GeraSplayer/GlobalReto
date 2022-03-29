package com.example.global.classes;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.example.global.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Item {
    private String IId;
    private String INombre;
    private String ICategoria;
    private String IDireccion;
    private int IRating;
    private String IRatingString;
    private String IRatingCount;
    private String IPrecio;
    private String ITelefono;
    private double ILatitud;
    private double ILongitud;
    private String IUrl;

    public Item(JSONObject jsonObject) throws JSONException {
        String categoria = "";
        JSONArray catArray = jsonObject.optJSONArray("categories");
        if(catArray != null) {
            for (int i = 0; i < catArray.length(); i++) {
                JSONObject object = (JSONObject) catArray.get(i);
                categoria += object.optString("title");
                if (i < catArray.length() - 1)
                    categoria += ", ";
            }
        }
        String direction = "";
        JSONObject locationObj = jsonObject.optJSONObject("location");
        if(locationObj !=null) {
            JSONArray dirArray = locationObj.optJSONArray("display_address");
            if (dirArray != null) {
                for (int i = 0; i < dirArray.length(); i++) {
                    direction += (String) dirArray.get(i);
                    if (i < dirArray.length() - 1)
                        direction += ", ";
                }
            }
        }
        JSONObject coordinates = jsonObject.optJSONObject("coordinates");

        this.IId = jsonObject.optString("id", "molinari-delicatessen-san-francisco");
        this.INombre = jsonObject.optString("name", "Molinari Delicatessen");
        this.ICategoria = categoria;
        this.IDireccion = direction;
        this.IRating = jsonObject.optInt("rating", 0);
        this.IRatingString = Integer.toString(this.IRating);
        this.IRatingCount = "("+jsonObject.optString("review_count", "0")+")";
        this.IPrecio = jsonObject.optString("price", "$$$$$");
        this.ITelefono = jsonObject.optString("display_phone", "8100000000");
        if(coordinates != null) {
            this.ILatitud = coordinates.optDouble("latitude", 0.0);
            this.ILongitud = coordinates.optDouble("longitude", 0.0);
        }
        this.IUrl = jsonObject.optString("image_url","https://www.yelp.com/biz/molinari-delicatessen-san-francisco");

    }

    public String getIId() {
        return IId;
    }
    public void setIId(String IId) {
        this.IId = IId;
    }
    public String getINombre() {
        return INombre;
    }
    public void setINombre(String INombre) {
        this.INombre = INombre;
    }
    public String getICategoria() {
        return ICategoria;
    }
    public void setICategoria(String ICategoria) {
        this.ICategoria = ICategoria;
    }
    public String getIDireccion() {
        return IDireccion;
    }
    public void setIDireccion(String IDireccion) {
        this.IDireccion = IDireccion;
    }
    public int getIRating() {
        return IRating;
    }
    public void setIRating(int IRating) {
        this.IRating = IRating;
    }
    public String getIRatingString() {
        return IRatingString;
    }
    public void setIRatingString(String IRatingString) {
        this.IRatingString = IRatingString;
    }
    public String getIRatingCount() {
        return IRatingCount;
    }
    public void setIRatingCount(String IRatingCount) {
        this.IRatingCount = IRatingCount;
    }
    public String getIPrecio() {
        return IPrecio;
    }
    public void setIPrecio(String IPrecio) {
        this.IPrecio = IPrecio;
    }
    public String getITelefono() {
        return ITelefono;
    }
    public void setITelefono(String ITelefono) {
        this.ITelefono = ITelefono;
    }
    public double getILatitud() {
        return ILatitud;
    }
    public void setILatitud(double ILatitud) {
        this.ILatitud = ILatitud;
    }
    public double getILongitud() {
        return ILongitud;
    }
    public void setILongitud(double ILongitud) {
        this.ILongitud = ILongitud;
    }
    public String getIUrl() {
        return IUrl;
    }
    public void setIUrl(String IUrl) {
        this.IUrl = IUrl;
    }

    @BindingAdapter("android:imageURL")
    public static void loadImage(ImageView view, String url){
        Glide.with(view)
                .load(url)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(view);

    }
}
