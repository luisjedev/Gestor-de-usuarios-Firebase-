package com.example.loggin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class MenuGoogle extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private ImageView imagen_google;
    private TextView nombre_google, email_google, id_google;

    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_google);

        imagen_google = (ImageView) findViewById(R.id.imagen_google);
        nombre_google = (TextView) findViewById(R.id.nombre_google);
        email_google = (TextView) findViewById(R.id.email_google);
        id_google = (TextView) findViewById(R.id.id_google);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();


        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso )
                .build();

    }

    @Override
    protected void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.
                silentSignIn(googleApiClient);

        if(opr.isDone()){
           GoogleSignInResult result = opr.get();
           handleSignInResult(result);
        }else{
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult result) {
                    handleSignInResult(result);
                }
            });
        }
    }

    private void handleSignInResult(GoogleSignInResult result){

        if(result.isSuccess()){
            GoogleSignInAccount account = result.getSignInAccount();
            nombre_google.setText(account.getDisplayName());
            email_google.setText(account.getEmail());
            id_google.setText(account.getId());

            Glide.with(this).load(account.getPhotoUrl()).into(imagen_google);
        }else{
            goLogInScreen();
        }

    }

    private void goLogInScreen(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
        | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void cerrarSesion(View view){
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if(status.isSuccess()){
                    goLogInScreen();
                }else{
                    Toast.makeText(MenuGoogle.this, "No se pudo cerrar sesion", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void revocar(View view){

        Auth.GoogleSignInApi.revokeAccess(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()){
                    goLogInScreen();
                }else{
                    Toast.makeText(MenuGoogle.this, "No se pudo revocar el acceso", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
