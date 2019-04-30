package com.aletz.prueba;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PantallaInicioSesion extends AppCompatActivity {
    EditText eTCor,eTCon;
    TextView tVOlv,tv9;
    Button btnIniSes;
    RequestQueue rQ;
    boolean encontrado;
    String con="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_pantalla_inicio_sesion);
        eTCor = findViewById(R.id.editTextBusqueda);
        eTCon = findViewById(R.id.editTextContrasena);
        btnIniSes = findViewById(R.id.buttonInicioSesion);
        tv9 = (TextView) findViewById(R.id.textView9);

        tv9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), Menu.class);
                startActivityForResult(intent, 0);
            }
        });

        btnIniSes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //192.168.43.227
                //192.168.1.78
                buscarCorreo("http://192.168.43.227/BMApp/recuperarCorreo.php?correo=" + eTCor.getText().toString());
            }
        });
    }

    private void buscarCorreo(String URL){
        JsonArrayRequest jsnar = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                encontrado=false;
                JSONObject jso = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jso = response.getJSONObject(i);
                        if (jso!=null){
                            buscarContrasena("http://192.168.43.227/BMApp/recuperarContra.php?correo="
                                    + eTCor.getText().toString());
                        }else{
                            Toast.makeText(PantallaInicioSesion.this, "El correo no coincide" +
                                    "intente de nuevo", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(PantallaInicioSesion.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PantallaInicioSesion.this, "Error de conexion", Toast.LENGTH_SHORT).show();
            }
        });
        rQ = Volley.newRequestQueue(this);
        rQ.add(jsnar);
    }

    private void buscarContrasena(String URL){
        JsonArrayRequest jsnar = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jso = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jso = response.getJSONObject(i);
                        con =jso.getString("contrasena");
                        String y=eTCon.getText().toString();
                        if(y.equals(con)) {
                            Toast.makeText(PantallaInicioSesion.this, "Datos correctos," +
                                    " bienvenido", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), Menu.class);
                            startActivityForResult(intent, 0);
                        }else{
                            Toast.makeText(PantallaInicioSesion.this, "La contraseña no coincide, " +
                                    "intente de nuevo.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(PantallaInicioSesion.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PantallaInicioSesion.this, "Error de conexion", Toast.LENGTH_SHORT).show();
            }
        });
        rQ = Volley.newRequestQueue(this);
        rQ.add(jsnar);
    }
}
