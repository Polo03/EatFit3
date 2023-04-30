package com.example.eatfitreal;

import static android.app.PendingIntent.getActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.security.Permission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Login extends AppCompatActivity {

    static EditText nick;
    private boolean isActivateRadioButton;
    private SharedPreferences preferences;

    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        nick=(EditText) findViewById(R.id.editTextUsuario);
        EditText pwd=(EditText) findViewById(R.id.editTextPwd);
        RadioButton radioButtonSesion=(RadioButton)findViewById(R.id.radioButtonSesion);
        TextView textViewOlvidarPwd=(TextView) findViewById(R.id.textViewOlvidarPwd);

        preferences=getSharedPreferences("Preferences",MODE_PRIVATE);
        introduceRutinas();
        introduceUserRoot();

        //Validamos si hay alguna sesion abierta, es decir, si hay algun usuario ya logeado ha querido mantener su sesión abierta
        validarSesion();

        isActivateRadioButton=radioButtonSesion.isChecked();
        radioButtonSesion.setOnClickListener(new View.OnClickListener() {
            //ACTIVADO
            @Override
            public void onClick(View view) {
                //hacemos esto para poder activar y descativar el botón de no cerrar sesion
                if(isActivateRadioButton)
                    radioButtonSesion.setChecked(false);

                isActivateRadioButton=radioButtonSesion.isChecked();
            }
        });


        //Boton para iniciar sesión
        Button botonInicioSesion=(Button)findViewById(R.id.buttonInicioSesion);
        botonInicioSesion.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                myRef = FirebaseDatabase.getInstance().getReference();

                myRef.child("Usuarios").addValueEventListener(new ValueEventListener() {
                    String nickString="";
                    String password="";
                    String email="";
                    int vecesLogeado=0;

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                            if(dataSnapshot.child("nick").getValue().toString().equals(nick.getText().toString()) || dataSnapshot.child("email").getValue().toString().equals(nick.getText().toString())){
                                nickString=dataSnapshot.child("nick").getValue().toString();
                                password=dataSnapshot.child("password").getValue().toString();
                                email=dataSnapshot.child("email").getValue().toString();
                                vecesLogeado=Integer.parseInt(dataSnapshot.child("VecesLogeado").getValue().toString());
                            }
                        }
                        if(password.equals(pwd.getText().toString())){
                            //Toast.makeText(Login.this, "Usuario Valido", Toast.LENGTH_SHORT).show();
                            if(isActivateRadioButton) {
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("nick", nickString);
                                editor.putString("password", pwd.getText().toString());
                                editor.commit();
                            }

                            //Si es la primera vez que se logea, se muestra un cuestionario
                            if(vecesLogeado==0){
                                Intent intent = new Intent(Login.this, Cuestionario.class);
                                startActivity(intent);
                            //Sino, se muestra el menu principal
                            }else{
                                Intent intent = new Intent(Login.this, MenuPrincipal.class);
                                startActivity(intent);
                            }
                        }else{
                            Toast.makeText(Login.this, "Usuario Invalido", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });

        //Si quiere crear una cuenta
        Button botonCreateAccount=(Button) findViewById(R.id.buttonCreateAccount);
        botonCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, CreateAccount.class);
                startActivity(intent);
            }
        });

    }

    //Lo que hacemos es validar si hay una sesión abierta
    private void validarSesion(){
        String nick=preferences.getString("nick",null);
        String password=preferences.getString("password",null);

        //Si hay sesion abierta nos lleva directamente a la ventana de menu principal
        if(nick!=null && password!=null){
            Intent intent = new Intent(Login.this, MenuPrincipal.class);
            startActivity(intent);
        }
    }

    //Devuelve el ultimo usuario logeado
    public String ultimoUsuarioLogeado(){
        return nick.getText().toString();
    }

    public void mostrarVentanaOlvidarContrasena(View v){
        Intent intent = new Intent(Login.this, OlvidoDeContrasena.class);
        startActivity(intent);
    }

    public void introduceRutinas(){

        myRef = FirebaseDatabase.getInstance().getReference();

        Map<String, Object> datosRutina1 = new HashMap<>();
        Map<String, Object> datosRutina2 = new HashMap<>();
        Map<String, Object> datosRutina3 = new HashMap<>();
        Map<String, Object> datosRutina4 = new HashMap<>();
        Map<String, Object> datosRutina5 = new HashMap<>();
        datosRutina1.put("NumRutina","1");
        datosRutina1.put("Descripción", "Rutina 1");
        datosRutina2.put("NumRutina","2");
        datosRutina2.put("Descripción", "Rutina 2");
        datosRutina3.put("NumRutina","3");
        datosRutina3.put("Descripción", "Rutina 3");
        datosRutina4.put("NumRutina","4");
        datosRutina4.put("Descripción", "Rutina 4");
        datosRutina5.put("NumRutina","5");
        datosRutina5.put("Descripción", "Rutina 5");
        myRef.child("Rutinas").child("Rutina1").setValue(datosRutina1);
        myRef.child("Rutinas").child("Rutina2").setValue(datosRutina2);
        myRef.child("Rutinas").child("Rutina3").setValue(datosRutina3);
        myRef.child("Rutinas").child("Rutina4").setValue(datosRutina4);
        myRef.child("Rutinas").child("Rutina5").setValue(datosRutina5);

    }

    public void introduceUserRoot(){

        myRef = FirebaseDatabase.getInstance().getReference();

        Map<String, Object> datosRoot = new HashMap<>();
        datosRoot.put("nick","Root");
        datosRoot.put("password","Root");
        datosRoot.put("email","poloadrian3@gmail.com");
        datosRoot.put("DNI","47318093D");
        datosRoot.put("Peso",90);
        datosRoot.put("Altura",170);
        datosRoot.put("FechaNac","17/10/2003");
        datosRoot.put("Phone","601361984");
        datosRoot.put("VecesLogeado",1);
        datosRoot.put("NumRutina",0);
        myRef.child("Usuarios").child("Root").setValue(datosRoot);
    }

}