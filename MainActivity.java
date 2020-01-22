package com.example.loggin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private TextInputEditText usuario,contraseña;
    private DatabaseReference ref;
    private StorageReference sto;
    private GoogleApiClient googleApiClient;
    private SignInButton boton_google;
    public static final int SIGN_IN_CODE = 777;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        boton_google = (SignInButton) findViewById(R.id.boton_google);
        boton_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, SIGN_IN_CODE);
            }
        });



        usuario=(TextInputEditText) findViewById(R.id.usuario);
        contraseña=(TextInputEditText) findViewById(R.id.contraseña);


        ref = FirebaseDatabase.getInstance().getReference();
        sto = FirebaseStorage.getInstance().getReference();


        boton_google.setSize(SignInButton.SIZE_WIDE);
        boton_google.setColorScheme(SignInButton.COLOR_DARK);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SIGN_IN_CODE){
           GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
           handleSignInResult(result);
        }
    }

    private  void handleSignInResult(GoogleSignInResult result){
        System.out.println(result.isSuccess());
            if(result.isSuccess()){
                goMainScreen();
            }else{
                Toast.makeText(this, "No se pudo iniciar sesion", Toast.LENGTH_SHORT).show();
            }
    }

    private void goMainScreen() {
        Intent intent = new Intent(this, MenuGoogle.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void registro(View view){
        Intent i = new Intent(this,Registro.class);
        startActivity(i);
    }

    public void menuPrincipal(View view){

        final String valor_usuario = usuario.getText().toString();
        final String valor_contraseña = contraseña.getText().toString();

        if (valor_usuario.equals("") || valor_contraseña.equals("")) {
            Toast.makeText(this, "Completa los campos necesarios", Toast.LENGTH_LONG).show();
        } else {
            ref.child("cuentas")
                    .child("usuarios")
                    .orderByChild("nombre")
                    .equalTo(valor_usuario)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChildren()) {

                                System.out.println("EXISTE");
                                DataSnapshot hijo=dataSnapshot.getChildren().iterator().next();
                                String contraseña_verdadera = hijo.getValue(Usuario.class).getContraseña();
                                String tipo_usuario = hijo.getValue(Usuario.class).getTipo();
                                String fecha_usuario = hijo.getValue(Usuario.class).getFecha_creacion();



                                if (contraseña_verdadera.equals(valor_contraseña)){

                                    if (tipo_usuario.equals("admin")){

                                        Intent i = new Intent(MainActivity.this,MenuAdministrador.class);
                                        startActivity(i);
                                        Toast.makeText(getApplicationContext(), "Bienvenido", Toast.LENGTH_LONG).show();
                                    }else{
                                        Intent i = new Intent(MainActivity.this,MenuPrincipal.class);
                                        i.putExtra("id_usuario",hijo.getValue(Usuario.class).getId());
                                        i.putExtra("fecha_usuario",fecha_usuario);
                                        startActivity(i);
                                        Toast.makeText(getApplicationContext(), "Bienvenido", Toast.LENGTH_LONG).show();
                                    }
                                }else{
                                    
                                    contraseña.setError("Usuario o Contraseña no coinciden");

                                }

                            } else {

                                System.out.println("NO EXISTE");
                                Toast.makeText(getApplicationContext(), "El Usuario o Contraseña no coinciden", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
