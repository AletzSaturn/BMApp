package com.aletz.prueba;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.util.List;
import java.util.Locale;

public class Menu extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    double lat,lon;
    LatLng latLng;
    EditText edT;
    Button btnBus,btnMenu;
    FusedLocationProviderClient fLPC;
    String ip="192.168.137.1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_menu);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        edT = findViewById(R.id.editTextBusqueda);
        btnBus = findViewById(R.id.botonBusqueda);
        btnMenu = findViewById(R.id.botonMenu);

        getDeviceLocation();

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              String numCam= edT.getText().toString();
              if(numCam.equals("1") || numCam.equals("Potrerillos")){
                  mMap.clear();
                  buscarCoordenada("http://"+ip+"/BMApp/recuperarCoord.php?idRuta=1");
              }else if(numCam.equals("4") || numCam.equals("Lienzo Charro")){
                  mMap.clear();
                  buscarCoordenada("http://"+ip+"/BMApp/recuperarCoord.php?idRuta=4");
              }else{
                  Toast.makeText(Menu.this, "La ruta que ingresó no existe, intente de nuevo.", Toast.LENGTH_SHORT).show();
              }

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);

        btnBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    direccion(latLng);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(Menu.this, "Por favor, ingrese una dirección válida.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void direccion(LatLng latLng) throws IOException{

        Geocoder geo = new Geocoder(this);
        int maxResultados = 1;

        List<Address> list = geo.getFromLocationName(edT.getText().toString(),maxResultados);
        Address add = list.get(0);
        String localidad = add.getLocality();
        Toast.makeText(this, localidad, Toast.LENGTH_SHORT).show();

        lat=add.getLatitude();
        lon=add.getLongitude();

        LatLng destino = new LatLng(lat,lon);

        mMap.addMarker(new MarkerOptions().position(destino).title(edT.getText().toString()));

        goToLocation(lat,lon,15);

    }

    public void goToLocation(double lat,double lng, float zoom){
        LatLng ll = new LatLng(lat,lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll,zoom);
        mMap.moveCamera(update);
    }

    public void getDeviceLocation() throws NullPointerException{
        fLPC = LocationServices.getFusedLocationProviderClient(this);
        Task location = fLPC.getLastLocation();
        location.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    Location currentLocation = (Location) task.getResult();
                    goToLocation(currentLocation.getLatitude(),currentLocation.getLongitude(),18);
                }else{
                    Toast.makeText(Menu.this, "No se encontró tu ubicación actual, ¿Tu GPS está encendido?", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /*
    LatLng sydney = new LatLng(-34, 151);
    mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
    mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    private void buscarCoordenada(String URL){

        JsonArrayRequest jsnar = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jso = null;
                double lat=0,lat1=0;
                double lon=0,lon1=0;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        if (i>0){
                        lat1=lat;
                        lon1=lon;
                        }
                        jso = response.getJSONObject(i);
                        lat=Double.parseDouble(jso.getString("Latitud"));
                        lon=Double.parseDouble(jso.getString("Longitud"));
                        LatLng mark = new LatLng(lat,lon);
                        Marker m;
                        m = mMap.addMarker(new MarkerOptions().position(mark));
                        m.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.maker));
                        if(i>0){
                            Polyline line = mMap.addPolyline(new PolylineOptions()
                                    .add(new LatLng(lat,lon), new LatLng(lat1,lon1))
                                    .width(7)
                                    .color(Color.BLUE));
                        }
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error de conexion", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue rQ = Volley.newRequestQueue(this);
        rQ.add(jsnar);
    }

}
