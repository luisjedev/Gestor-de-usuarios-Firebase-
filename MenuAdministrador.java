package com.example.loggin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MenuAdministrador extends AppCompatActivity {



    private ListView lista;
    private DatabaseReference ref;
    private ArrayList<Usuario> items;
    private AdaptadorUsuario adaptador;
    private StorageReference sto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_administrador);

        lista=findViewById(R.id.lista_usuarios);
        items=new ArrayList<>();
        ref= FirebaseDatabase.getInstance().getReference();
        sto= FirebaseStorage.getInstance().getReference();

        ref.child("cuentas").child("usuarios").orderByChild("nombre").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                items.clear();
                for(DataSnapshot hijo:dataSnapshot.getChildren()) {
                    final Usuario usuario = hijo.getValue(Usuario.class);
                    usuario.setId(hijo.getKey());
                    items.add(usuario);
                }

                for(final Usuario usuario:items){
                    sto.child("cuentas")
                            .child("imagenes")
                            .child(usuario.getId())
                            .getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    usuario.setUrl_heroe(uri);
                                    adaptador.notifyDataSetChanged();
                                }
                            });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        adaptador=new AdaptadorUsuario(getApplicationContext(),android.R.layout.simple_list_item_1,items);
        lista.setAdapter(adaptador);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Usuario pojo_hijo=(Usuario) adapterView.getItemAtPosition(i);

            }
        });
    }


    }

