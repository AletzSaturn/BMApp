package com.aletz.prueba;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class AdminMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    double lat, lon;
    JSONObject jso;
    Boolean actualPosition=true;
    LatLng latLng;
    EditText edT;
    Button btnBus, btnMenu;
    FusedLocationProviderClient fLPC;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_admin_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        edT = (EditText) findViewById(R.id.editTextBusqueda2);
        btnBus = (Button) findViewById(R.id.botonBusqueda2);

        getDeviceLocation();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                String calle="";
                lat = latLng.latitude;
                lon = latLng.longitude;
                try {
                     calle=direccionCoordenada(lat,lon);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(lat + "\n" + lon +"\n"+calle);
                String url ="https://maps.googleapis.com/maps/api/directions/json?origin="+lat+","+lon+"&destination=20.337451171349652,-102.02619910240173&key=AIzaSyAsAkD0POqI9QXpwBeUgaBylZ0Zn2jxUwI";

                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            jso = new JSONObject(response);
                            trazarRuta(jso);
                            Log.i("jsonRuta: ",""+response);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
                queue.add(stringRequest);
            }
        });

        btnBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    direccion(latLng);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(AdminMap.this, "Por favor, ingrese una dirección válida.", Toast.LENGTH_SHORT).show();
                }
                // LLAMAR ACTIVIDAD DESDE FRAGMENTO   Intent intent = new Intent (getApplicationContext(), PantallaInicioSesion.class);
                //   startActivity(intent);
            }
        });
    }

    public void direccion(LatLng latLng) throws IOException {
        Geocoder geo = new Geocoder(this);
        int maxResultados = 1;
        List<Address> list = geo.getFromLocationName(edT.getText().toString(), maxResultados);
        Address add = list.get(0);
        String localidad = add.getLocality();
        Toast.makeText(this, localidad, Toast.LENGTH_SHORT).show();

        lat = add.getLatitude();
        lon = add.getLongitude();

        LatLng destino = new LatLng(lat, lon);

        mMap.addMarker(new MarkerOptions().position(destino).title(edT.getText().toString()));
        goToLocation(lat, lon, 15);

    }

    public String direccionCoordenada(double lat, double lon) throws IOException {
        String calle="";
        Geocoder geo = new Geocoder(this);
        int maxResultados = 1;
        List<Address> list = geo.getFromLocation(lat,lon, maxResultados);
        Address add = list.get(0);
        calle = add.getAddressLine(0);
        return calle;
    }

    public void goToLocation(double lat, double lng, float zoom) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        mMap.moveCamera(update);
    }

    public void getDeviceLocation() {
        fLPC = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Task location = fLPC.getLastLocation();
        location.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    System.out.println("Locación encontrada");
                    Location currentLocation = (Location) task.getResult();
                    goToLocation(currentLocation.getLatitude(),currentLocation.getLongitude(),18);
                }else{
                    System.out.println("Locación no encontrada");
                    Toast.makeText(AdminMap.this, "No se encontró tu ubicación actual, ¿Tu GPS está encendido?", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void trazarRuta(JSONObject jso) {
        JSONArray jRoutes;
        JSONArray jLegs;
        JSONArray jSteps;
        try {
            jRoutes = jso.getJSONArray("routes");
            for (int i=0; i<jRoutes.length();i++){
                jLegs = ((JSONObject)(jRoutes.get(i))).getJSONArray("legs");
                for (int j=0; j<jLegs.length();j++){
                    jSteps = ((JSONObject)jLegs.get(j)).getJSONArray("steps");
                    for (int k = 0; k<jSteps.length();k++){
                        String polyline = ""+((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                        Log.i("end",""+polyline);
                        List<LatLng> list = PolyUtil.decode(polyline);
                        mMap.addPolyline(new PolylineOptions().addAll(list).color(Color.GRAY).width(5));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
