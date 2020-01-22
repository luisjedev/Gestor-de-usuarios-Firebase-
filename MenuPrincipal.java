package com.example.loggin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.regex.Pattern;

public class MenuPrincipal extends AppCompatActivity {

    private TextView editar_nombre, editar_contraseña, editar_email;
    private ImageView editar_foto;
    private String id,fecha;
    private Uri foto_url;
    private DatabaseReference ref;
    private StorageReference sto;
    private final static int SELECCIONAR_FOTO=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        foto_url=null;
        editar_nombre = (TextView) findViewById(R.id.nombre_usuario);
        editar_foto = (ImageView) findViewById(R.id.imagen_usuario);
        editar_contraseña = (TextView) findViewById(R.id.contraseña_usuario);
        editar_email = (TextView) findViewById(R.id.email_usuario);


        Intent intent=getIntent();
        id=intent.getStringExtra("id_usuario");
        fecha=intent.getStringExtra("fecha_usuario");


        ref= FirebaseDatabase.getInstance().getReference();
        sto= FirebaseStorage.getInstance().getReference();

        ref.child("cuentas")
                .child("usuarios")
                .child(id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Usuario resultado=dataSnapshot.getValue(Usuario.class);


                        editar_nombre.setText(resultado.getNombre());
                        editar_contraseña.setText(resultado.getContraseña());
                        editar_email.setText(resultado.getEmail());


                        sto.child("cuentas").child("imagenes").child(id).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Glide.with(getApplicationContext()).load(uri).into(editar_foto);
                                foto_url=uri;
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



    }


    public void modificarUsuario(View v){

        final String valor_nombre=editar_nombre.getText().toString();
        final String valor_contraseña=editar_contraseña.getText().toString();
        final String valor_email=editar_email.getText().toString();
        final Handler mWaitHandler = new Handler();

        if (valor_nombre.equals("") || valor_contraseña.equals("") || valor_email.equals("")) {
            Toast.makeText(this, "Completa los campos necesarios", Toast.LENGTH_LONG).show();
        } else {

            ref.child("cuentas")
                    .child("usuarios")
                    .orderByChild("nombre")
                    .equalTo(valor_nombre)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.hasChildren()) {

                                DataSnapshot hijo=dataSnapshot.getChildren().iterator().next();


                                    if (hijo.getValue(Usuario.class).getId().equals(id)){

                                        if (foto_url != null) {
                                            if (validarEmail(valor_email)) {
                                                Usuario nuevo_heroe = new Usuario(valor_nombre, valor_contraseña, valor_email, fecha);
                                                nuevo_heroe.setId(id);


                                                ref.child("cuentas").child("usuarios").child(id).setValue(nuevo_heroe);
                                                sto.child("cuentas").child("imagenes").child(id).putFile(foto_url);

                                                Toast.makeText(getApplicationContext(), "Usuario editado con exito", Toast.LENGTH_LONG).show();

                                                mWaitHandler.postDelayed(new Runnable() {

                                                    @Override
                                                    public void run() {

                                                        try {
                                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                            startActivity(intent);
                                                        } catch (Exception ignored) {
                                                            ignored.printStackTrace();
                                                        }
                                                    }
                                                }, 1000);  // Give a 5 seconds delay.
                                            }else{
                                                Toast.makeText(MenuPrincipal.this, "Email inválido", Toast.LENGTH_SHORT).show();
                                            }

                                        } else{

                                            Toast.makeText(MenuPrincipal.this, "No se ha seleccionado una imagen", Toast.LENGTH_LONG).show();
                                        }
                                    }else{

                                        Toast.makeText(getApplicationContext(), "El Usuario ya existe", Toast.LENGTH_LONG).show();

                                    }

                            } else {

                                if (foto_url != null) {

                                    if (validarEmail(valor_email)) {
                                        Usuario nuevo_heroe = new Usuario(valor_nombre, valor_contraseña, valor_email, fecha);
                                        nuevo_heroe.setId(id);

                                        ref.child("cuentas").child("usuarios").child(id).setValue(nuevo_heroe);
                                        sto.child("cuentas").child("imagenes").child(id).putFile(foto_url);

                                        Toast.makeText(getApplicationContext(), "Usuario editado con exito", Toast.LENGTH_LONG).show();

                                        mWaitHandler.postDelayed(new Runnable() {

                                            @Override
                                            public void run() {

                                                try {
                                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                    startActivity(intent);
                                                } catch (Exception ignored) {
                                                    ignored.printStackTrace();
                                                }
                                            }
                                        }, 1000);  // Give a 5 seconds delay.

                                    }else{
                                        Toast.makeText(MenuPrincipal.this, "Email inválido", Toast.LENGTH_SHORT).show();
                                    }

                                } else{

                                    Toast.makeText(MenuPrincipal.this, "No se ha seleccionado una imagen", Toast.LENGTH_LONG).show();
                                }


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }

    }

    public void seleccionarFotoUsuario(View v){

        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,SELECCIONAR_FOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==SELECCIONAR_FOTO && resultCode==RESULT_OK){
            foto_url=data.getData();
            editar_foto.setImageURI(foto_url);
            Toast.makeText(getApplicationContext(),"Imagen seleccionada",Toast.LENGTH_LONG).show();

        }else{
            Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG).show();
        }
    }

    private boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }



}
