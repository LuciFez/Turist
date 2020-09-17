package com.example.turist_0_1_a.loginpackage.places;

import com.example.turist_0_1_a.loginpackage.ListActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParser {

    private HashMap<String,String> getPlace(JSONObject j){

        HashMap<String,String> gPlaceMap = new HashMap<>();
        String name="";
        String vecinity="";
        String latitude="";
        String longitude="";
        String ref="";
        try {
            if(!j.isNull("name")){
                name=j.getString("name");//ia numele din json obj
            }
            if(!j.isNull("vicinity")){
                vecinity=j.getString("vicinity");//ia adresa din json obj
            }

            latitude = j.getJSONObject("geometry").getJSONObject("location").getString("lat");//ia latitudinea din json obj
            longitude = j.getJSONObject("geometry").getJSONObject("location").getString("lng");//ia longitudinea din json obj

            ref = j.getString("reference");//ia references din json obj

            gPlaceMap.put("name",name);//adauga nume in hashmap
            gPlaceMap.put("vec",vecinity);
            gPlaceMap.put("lat",latitude);
            gPlaceMap.put("lng",longitude);
            gPlaceMap.put("ref",ref);
            if(j.has("rating")) {
                gPlaceMap.put("rating", j.getString("rating"));//daca are rating adauga in hash map
            }else{
                gPlaceMap.put("rating", "-");//daca nu are
            }
            if(j.has("opening_hours")) {
                gPlaceMap.put("status", j.getJSONObject("opening_hours").getString("open_now"));//daca are open hours adauga in hash map
            }else{
                gPlaceMap.put("status", "No opening hours");//daca nu are
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return gPlaceMap;

    }

    private List<HashMap<String,String>> getPlaces(JSONArray j){

        int count= j.length();//lungimea jArray ului
        List<HashMap<String,String>> placeList = new ArrayList<>();
        HashMap<String,String> place= null;

        for(int i=0;i<count;i++) {
            try {
                place = getPlace((JSONObject) j.get(i));//hashmap cu valorile din jArray
                placeList.add(place);//adauga hashmap in lista
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return placeList;//return lista
    }

    public List<HashMap<String,String>> parse(String s){
        JSONArray jArray = null;
        JSONObject jObj = null;

        try{
            jObj = new JSONObject(s);
            jArray = jObj.getJSONArray("results");
        }catch (Exception e){
            e.printStackTrace();
        }
        return getPlaces(jArray);

    }

}
