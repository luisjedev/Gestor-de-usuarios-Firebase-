package com.example.loggin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;

public class Registro extends AppCompatActivity {

    private ImageView foto_usuario;
    private TextInputEditText usuario,contraseña,email;
    private Uri foto_url;
    private final static int SELECCIONAR_FOTO = 1;
    private DatabaseReference ref;
    private StorageReference sto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        usuario = (TextInputEditText) findViewById(R.id.usuario);
        contraseña = (TextInputEditText) findViewById(R.id.contraseña);
        email = (TextInputEditText) findViewById(R.id.email);
        foto_usuario = (ImageView) findViewById(R.id.foto_usuario);

        ref = FirebaseDatabase.getInstance().getReference();
        sto = FirebaseStorage.getInstance().getReference();
        foto_url = null;

    }


    public void crearCuenta(View v) {
        final String valor_usuario = usuario.getText().toString();
        final String valor_contraseña = contraseña.getText().toString();
        final String valor_email = email.getText().toString();



        if (valor_usuario.equals("") || valor_contraseña.equals("") || valor_email.equals("")) {
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
                                Toast.makeText(getApplicationContext(), "El Usuario ya existe", Toast.LENGTH_LONG).show();
                            } else {
                                if (foto_url != null) {
                                    if(validarEmail(valor_email)) {
                                        Calendar calendar = Calendar.getInstance();

                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                                        String fecha_creacion = sdf.format(calendar.getTime());


                                        Usuario nuevo_usuario = new Usuario(valor_usuario, valor_contraseña, valor_email, fecha_creacion);

                                        String clave = ref.child("cuentas").child("usuarios").push().getKey();
                                        nuevo_usuario.setId(clave);

                                        ref.child("cuentas").child("usuarios").child(clave).setValue(nuevo_usuario);
                                        sto.child("cuentas").child("imagenes").child(clave).putFile(foto_url);
                                        Toast.makeText(Registro.this, "Usuario creado con exito", Toast.LENGTH_LONG).show();

                                        Intent i = new Intent(Registro.this, MainActivity.class);
                                        startActivity(i);

                                    }else{
                                        Toast.makeText(Registro.this, "Email inválido", Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    Toast.makeText(Registro.this, "No se ha seleccionado una imagen", Toast.LENGTH_LONG).show();
                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }
    }

    public void seleccionarFoto (View v){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, SELECCIONAR_FOTO);
    }

    @Override
    protected void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECCIONAR_FOTO && resultCode == RESULT_OK) {
            foto_url = data.getData();
            foto_usuario.setImageURI(foto_url);
            Toast.makeText(getApplicationContext(), "Imagen seleccionada", Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
        }
    }

    private boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }
}
