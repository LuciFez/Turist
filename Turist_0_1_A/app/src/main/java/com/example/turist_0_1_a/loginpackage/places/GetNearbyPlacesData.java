package com.example.turist_0_1_a.loginpackage.places;

import android.graphics.LightingColorFilter;

import com.example.turist_0_1_a.loginpackage.ListActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class GetNearbyPlacesData extends Thread {

    private String placeData;
    private String url;
    private GoogleMap gMap;

    private List<HashMap<String,String>> nearbyPlaceList;

    public void run(){

        DownloadUrl d = new DownloadUrl();//seteaza un obiect de tipul DownloadUrl
        try {
            placeData = d.readUrl(url);//apeleaza metoda readUrl() si trimite url ul din api
        } catch (IOException e) {
            e.printStackTrace();
        }

        nearbyPlaceList = null;

        DataParser parser = new DataParser();//parser pentru string json

        nearbyPlaceList = parser.parse(placeData);//se transforma intr-o lista da hashmapuri cu valorile dorite din json

        for(HashMap<String,String> i: nearbyPlaceList){//seteaza in ListActivity tot ce este nevoie pentru locatiile gasite

            ListActivity.locationsName.add(i.get("name"));
            ListActivity.latitudeList.add(Double.valueOf(i.get("lat")));
            ListActivity.longitudeList.add(Double.valueOf(i.get("lng")));
            ListActivity.vicinityList.add(i.get("vec"));
            ListActivity.openStatusList.add(i.get("status"));
            ListActivity.ratingList.add(i.get("rating"));

        }
    }

    public void set(GoogleMap g,String u){//seteaza mapa si url ul
        gMap = g;
        url = u;
        System.out.println(url);//google api link --- nu intoarce tot timpul, limita free user
    }

}
