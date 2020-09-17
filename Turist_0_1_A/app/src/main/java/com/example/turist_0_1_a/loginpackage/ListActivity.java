package com.example.turist_0_1_a.loginpackage;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.turist_0_1_a.R;
import com.example.turist_0_1_a.loginpackage.places.GetNearbyPlacesData;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ListActivity extends AppCompatActivity implements OnMapReadyCallback {

    private boolean detailsValue;
    private Switch detailsSwitch;

    private String distance;
    private String destination;
    private String family;

    private static ListView listView;
    private Button back;

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    private MapView mapView;
    private GoogleMap gMap;

    //google stuff

    private boolean mLocationPermissionGranted;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9002;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9003;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    private double latitude;
    private double longitude;

    public static List<String> locationsName = new ArrayList<String>();
    public static List<Double> latitudeList = new ArrayList<Double>();
    public static List<Double> longitudeList = new ArrayList<Double>();

    public static List<String> vicinityList = new ArrayList<String>();
    public static List<String> openStatusList = new ArrayList<String>();
    public static List<String> ratingList = new ArrayList<String>();

    //google stuff

    // sms stuff

    private final Activity thisActivity = this;

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;
    private String phoneNo;
    private String sms;

    private TextView alertTextView;

    // sms stuff


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        ActivityCompat.requestPermissions(thisActivity,new String[]{Manifest.permission.SEND_SMS},1); //permisie sms

        alertTextView = (TextView) findViewById(R.id.AlertTextView); //gaseste alerta

        detailsValue = false;

        if (getIntent().hasExtra("destination")) {
            destination = getIntent().getExtras().get("destination").toString();//seteaza de pe ce tab s-a venit
        }
        if (getIntent().hasExtra("distance")) {
            distance = getIntent().getExtras().get("distance").toString();//seteaza raza de cautare din fragmentul precedent
        }
        if (getIntent().hasExtra("family")) {
            family = getIntent().getExtras().get("family").toString();
        }
        //ia din intent

        detailsSwitch = (Switch) findViewById(R.id.sms_details);
        detailsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                detailsValue = b;
            }
        });//listener switch details

        back = (Button) findViewById(R.id.backToFragment);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationsName.clear();
                latitudeList.clear();
                longitudeList.clear();
                Intent contentIntent = new Intent(getApplicationContext(), TabControlActivity.class);
                startActivity(contentIntent);
            }
        });//listener back button

        listView = (ListView) findViewById(R.id.listView);

        //google stuff

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);//apeleaza metodele onStart,onResume etc. in ordine
        mapView.getMapAsync(this);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this); //gaseste locatia pe harta

    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        if(checkMapServices()){//daca are mapServices updatate in telefon
            if(mLocationPermissionGranted){//daca are permisie sa acceseze locatia
                getLastKnownLocation(); //daca are permisie iti ia locatia curenta
            }else{
                getLocationPermission();//daca nu are permisie
            }
        }
    }

    private boolean checkMapServices(){
        if(isServicesOK()){
            if(isMapsEnabled()){
                return true;
            }
        }
        return false;
    }

    //daca google services e updatat corect pe device
    public boolean isServicesOK(){
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public boolean isMapsEnabled(){
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();//daca nu e deschis gps ul
            return false;
        }
        return true;
    }



    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            getLastKnownLocation(); // dupa ce are permisiune sa acceseze locatia iti ia locatia curenta
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void getLastKnownLocation() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }//verifica daca are permisiuni la locatie
        mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {//listener pe locationFinder
            @Override
            public void onComplete(@NonNull Task<Location> task) {//cand a luat-o complet
                if (task.isSuccessful()) {//daca a luat-o cu succes
                    Location location = task.getResult();//creaza location obj
                    LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude()); //creaza latlng object cu locatia curenta

                    latitude = location.getLatitude();//seteaza latitudinea actuala
                    longitude = location.getLongitude();//seteaza longitudinea actuala --- vor fi folosite mai tarziu

                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15f));//muta camera pe locatia actuala la zoom de 15
                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    gMap.setMyLocationEnabled(true);//seteaza marcker vizibil pentru locaatia curenta
                    useList(); // urmeaza lista - parctic aplicatia in sine
                }
            }
        });
    }

    private void useList() {
        findCloseLocations();//gaseste locatiile apropiate
    }

    private void findCloseLocations(){

        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();//instantiaza obiect de tipul GetNearbyPlacesData ce extinde clasa thread


        /*
        pentru fiecare tip de destinatie se executa acelasi lucru, destinatia fiind diferita
         */


        if(destination.equals("atm")){
            gMap.clear();//curata markerele ce au fost deja puse pe mapa pana acum
            String url = getURL(latitude,longitude,"atm");////creeaza url pentru API

            getNearbyPlacesData.set(gMap,url);//seteaza mapa si url pentru cautarea in api

            getNearbyPlacesData.start();//da start la thread(nu poti folosi network in thread ul principal)

            try {
                getNearbyPlacesData.join();//wait pana cand threadul a terminat
            } catch (InterruptedException e) {
                System.out.println("Eroare la join --- getNearByPlaces check wait thread dead ---");
                e.printStackTrace();
            }

            putOnMap();//continua executia
        }else if(destination.equals("park")){
            gMap.clear();
            String url = getURL(latitude,longitude,"park");

            getNearbyPlacesData.set(gMap,url);

            getNearbyPlacesData.start();

            try {
                getNearbyPlacesData.join();
            } catch (InterruptedException e) {
                System.out.println("Eroare la join --- getNearByPlaces check wait thread dead ---");
                e.printStackTrace();
            }

            putOnMap();
        }else if(destination.equals("museum")){
            gMap.clear();
            String url = getURL(latitude,longitude,"museum");

            getNearbyPlacesData.set(gMap,url);

            getNearbyPlacesData.start();

            try {
                getNearbyPlacesData.join();
            } catch (InterruptedException e) {
                System.out.println("Eroare la join --- getNearByPlaces check wait thread dead ---");
                e.printStackTrace();
            }

            putOnMap();
        }else if(destination.equals("restaurant")){
            gMap.clear();
            String url = getURL(latitude,longitude,"restaurant");

            getNearbyPlacesData.set(gMap,url);

            getNearbyPlacesData.start();

            try {
                getNearbyPlacesData.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            putOnMap();
        }
    }

    private String getURL(double lati,double longi,String dest){

        StringBuilder sURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");//link default pentru google nearby care intoarce un json
        sURL.append("location="+lati+","+longi);//adauga latitudinea si longitudinea
        double d = Double.parseDouble(distance);
        d= d*1000;
        if(d>20000.0){
            d=5000.0;
        }//transforma distanta in cea dorita
        distance = String.valueOf(d);//o face string
        sURL.append("&radius="+distance);//add distanta
        sURL.append("&type="+dest);//add destinatie
        sURL.append("&sensor=true");
        sURL.append("&key=AIzaSyC8Bci8z6omBuCgvikn4ugueWzc9Kxa4jI");//API key

        return sURL.toString();//cast to string
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {//cand mapa nue e nula si poate fi folosita

        gMap = googleMap;

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        gMap.setMyLocationEnabled(true);

    }

    private void putOnMap(){

        for(int i=0;i<latitudeList.size();i++){//adauga fiecare finding pe harta
            MarkerOptions markerOptions = new MarkerOptions();

            String name = locationsName.get(i);//nume
            double lat = latitudeList.get(i);//lat
            double lng = longitudeList.get(i);//long

            LatLng latLng = new LatLng(lat,lng);//locatie pe harta
            markerOptions.position(latLng);//seteaza pozitia
            markerOptions.title(name);//seteaza numele
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));//seteaza marker icon

            gMap.addMarker(markerOptions);//adauga
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));//seteaza camera pe el la zoom 10

        }

        String namesList[] =new String[latitudeList.size()];//String vector pentru locatii
        for(int i=0;i<latitudeList.size();i++) namesList[i]=locationsName.get(i);//adauga in vector
        listView.setAdapter(new ArrayAdapter<String>(this,R.layout.list_view_text,namesList));//seteaza adapter pentru lista de locatii, are nevoie de String vector nu array

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {//listener pentru click item din lista
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if(detailsValue == false) {//daca nu se vor detalii, se trimite sms si se pozitioneaza pe el cu un Toast pentru distanta mai mica sau mai mare de 1km

                    LatLng latLng = new LatLng(latitudeList.get(i), longitudeList.get(i));//locatie lat lng
                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));//seteaza pe locatie cu zoom de 15

                    Location myLocation = new Location("myLocation");//pozitia mea, pentru distanta de obiectiv
                    myLocation.setLatitude(latitude);
                    myLocation.setLongitude(longitude);

                    Location destination = new Location("destination");//pozitia obiectiv, pentru distanta de pozitia mea
                    destination.setLatitude(latitudeList.get(i));
                    destination.setLongitude(longitudeList.get(i));

                    int duration = Toast.LENGTH_SHORT;//Toast duration
                    if (myLocation.distanceTo(destination) < 1000) {//daca e mai mica de 1 km
                        Toast toast = Toast.makeText(getApplicationContext(), "Less then 1000 meters", duration);//toast msg set
                        toast.show();//toast show

                        phoneNo = "+40753424103";//seteaza nr de telefon
                        sms = "Less then 1000 meters to " + locationsName.get(i);//seteaza mesaj

                        SmsManager smsManager = SmsManager.getDefault();//seteaza Obiect pentru sms

                        try {
                            smsManager.sendTextMessage(phoneNo, null, sms, null, null);//send msg
                        } catch (Exception eroare) {
                            //if troubles, we show the error message
                            eroare.printStackTrace();
                        }

                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(), "More then 1000 meters", duration);//daca e mai mult de 1km, apare doar Toast
                        toast.show();
                    }
                }else{//daca se vor detalii

                    AlertDialog.Builder builder = new AlertDialog.Builder(ListActivity.this);//Alert Object

                    String stat = "";//daca e deschis
                    if(openStatusList.get(i).equals("true")) {
                        stat = "Yes";
                    }else if(openStatusList.get(i).equals("false")) {
                        stat = "No";
                    }else{
                        stat = "-";//nu intoarce nimic api
                    }

                    builder.setCancelable(true);//poate fi inchis
                    builder.setTitle("Details:");//titlu
                    builder.setMessage("Address: "+vicinityList.get(i)+"\nOpen now: "+stat+"\nRating: "+ratingList.get(i)); //adresa + daca e deschis + rating

                    builder.setNegativeButton("", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {//buton cancel invisibil
                            dialogInterface.cancel();
                        }
                    });

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {//buton OK = am citit
                            alertTextView.setVisibility(View.VISIBLE);
                        }
                    });
                    builder.show();//afiseaza in alerta
                }
            }
        });
    }

    //alterta nu apare gps
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {//starteaza gps turn on
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    //permisie sms
    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String permissions[],@NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNo, null, sms, null, null);
                    Toast.makeText(getApplicationContext(), "SMS sent.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if(mLocationPermissionGranted){
                    getLastKnownLocation();//daca s-a activat gps ul
                }
                else{
                    getLocationPermission();//daca nu s-a activat cere sa se activeze
                }
            }
        }

    }

    // GOOGLE Methods

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

}
