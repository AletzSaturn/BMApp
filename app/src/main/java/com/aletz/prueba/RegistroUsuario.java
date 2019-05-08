package com.aletz.prueba;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class RegistroUsuario extends AppCompatActivity {

    EditText edTNom,edTCor,edTCon,edTCon2;
    Button btnRegistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_registro_usuario);
        edTNom =  findViewById(R.id.editTextNombre);
        edTCor =  findViewById(R.id.editTextCorreo);
        edTCon =  findViewById(R.id.editTextContrasena);
        edTCon2 = findViewById(R.id.editTextConfContrasena);
        btnRegistro = findViewById(R.id.buttonRegistro);

        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String con1=edTCon.getText().toString();
                String con2=edTCon2.getText().toString();
                if (con1.equals(con2)) {
                    ejecutarServicio("http://192.168.137.1/BMApp/insertarusuario.php");
                    Intent intent = new Intent(v.getContext(), PantallaPrincipal.class);
                    startActivityForResult(intent, 0);
                }else{
                    Toast.makeText(RegistroUsuario.this, "Las contraseñas no coinciden, intente de nuevo", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void ejecutarServicio(String URL){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "Se ha registrado con exito.", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> parametros = new HashMap<String, String>();
                parametros.put("Nombre",edTNom.getText().toString());
                parametros.put("Correo",edTCor.getText().toString());
                parametros.put("Contrasena",edTCon.getText().toString());
                return parametros;
            }
        };
        RequestQueue rQ = Volley.newRequestQueue(this);
        rQ.add(stringRequest);
    }
}
