package com.aletz.prueba;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PantallaPrincipal extends AppCompatActivity {

    Button btnIniSes, btnReg,btnIniAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_pantalla_principal);
        btnIniSes = (Button)findViewById(R.id.botonInicioSesion);
        btnReg = (Button)findViewById(R.id.botonRegistro);
        btnIniAdmin = (Button)findViewById(R.id.botonInicioSesionAdmin);

        btnIniSes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), PantallaInicioSesion.class);
                startActivityForResult(intent, 0);
            }
        });

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), RegistroUsuario.class);
                startActivityForResult(intent, 0);
            }
        });

        btnIniAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), AdminMap.class);
                startActivityForResult(intent, 0);
            }
        });
    }

}
