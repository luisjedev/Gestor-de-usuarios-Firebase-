package com.example.loggin;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class AdaptadorUsuario extends ArrayAdapter {

    private ImageButton boton;
    private DatabaseReference ref;
    private StorageReference sto;
    private Context context;
    private int resource;
    private ArrayList<Usuario> items;
    private TextView nombre, tipo, fecha, email, contraseña;
    private ImageView foto;

    public AdaptadorUsuario(Context context, int resource, ArrayList<Usuario> items){
        super(context,resource,items);

        this.context=context;
        this.resource=resource;
        this.items=items;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View vista=convertView;

        if(vista==null){
            vista= LayoutInflater.from(context).inflate(R.layout.elemento_lista,null);
        }

        nombre=vista.findViewById(R.id.nombre_usuario);
        contraseña=vista.findViewById(R.id.pass_usuario);
        email= vista.findViewById(R.id.email_usuario);
        tipo = vista.findViewById(R.id.tipo_usuario);
        fecha = vista.findViewById(R.id.fecha_usuario);
        boton = vista.findViewById(R.id.boton_borrar);

        foto=vista.findViewById(R.id.foto_usuario);

        ref= FirebaseDatabase.getInstance().getReference();
        sto= FirebaseStorage.getInstance().getReference();

        final Usuario pojo_usuario=items.get(position);
        nombre.setText(pojo_usuario.getNombre());
        contraseña.setText(pojo_usuario.getContraseña());
        email.setText(pojo_usuario.getEmail());
        tipo.setText(pojo_usuario.getTipo());
        fecha.setText(pojo_usuario.getFecha_creacion());

        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!pojo_usuario.getTipo().equals("admin")){

                    ref.child("cuentas").child("usuarios").child(pojo_usuario.getId()).removeValue();
                    sto.child("cuentas").child("imagenes").child(pojo_usuario.getId()).delete();

                }else{
                    Toast.makeText(context, "Usuarios administradores no pueden ser borrados", Toast.LENGTH_SHORT).show();
                }


            }
        });

        Glide.with(context).load(pojo_usuario.getUrl_heroe()).into(foto);

        return vista;
    }


}
