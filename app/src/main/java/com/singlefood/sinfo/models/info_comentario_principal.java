package com.singlefood.sinfo.models;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.singlefood.sinfo.R;
import com.singlefood.sinfo.models.productos.comentarios;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class info_comentario_principal extends Fragment {

    View view;
    String Key, id_user, tipo, precio,direccion;
    ArrayList<comentarios> arrayListComentarios;
    //Otro
    @BindView( R.id.text_view_info_direccion )
    TextView textview_direccion;
    @BindView( R.id.text_view_info_precio )
    TextView textview_precio;
    @BindView( R.id.text_view_info_tipo_comida )
    TextView textview_tipo_comida;
    @BindView( R.id.rating_info_text_rating )
    TextView textview_rating;
    //Otro comentario principal
    @BindView( R.id.rv_comentarios_name_user_principal )
    TextView textview_nombre_user;
    @BindView( R.id.rv_comentarios_text_user_comentarios_principal )
    TextView textview_comentarios_user;
    @BindView( R.id.rv_comentarios_imagen_user_image_principal )
    ImageView imageview_user;
    @BindView( R.id.rv_comentarios_text_user_fech_principal )
    TextView textview_fech;


    public info_comentario_principal() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach( context );
         Key = getArguments().getString("key");
         id_user = getArguments().getString("idUser");
        tipo = getArguments().getString("tipo");
        precio = getArguments().getString("precio");
        direccion = getArguments().getString("direccion");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate( R.layout.fragment_info_comentario_principal, container, false );
        ButterKnife.bind( this,view );
        getdatacoment();

        return view;
    }
    private void getdatacoment() {
        DatabaseReference coment_principal= FirebaseDatabase.getInstance().getReference("platillos").child( Key ).child( "comentarios" );

        coment_principal.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayListComentarios= new ArrayList<>();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    comentarios coment=snapshot.getValue( comentarios.class);
                    if(coment.getPrioridad()==1){
                        arrayListComentarios.add( coment );
                        textview_nombre_user.setText( "" );
                        textview_rating.setText(  String.valueOf( coment.getRating()  ));
                        textview_direccion.setText( direccion );
                        textview_precio.setText( precio );
                        textview_tipo_comida.setText( tipo );
                        textview_comentarios_user.setText( coment.getTexto() );
                        break;
                    }
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
    }

}
