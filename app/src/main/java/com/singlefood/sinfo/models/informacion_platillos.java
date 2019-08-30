package com.singlefood.sinfo.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.singlefood.sinfo.R;
import com.singlefood.sinfo.models.productos.Comentarios;
import com.singlefood.sinfo.models.productos.Platillos;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class informacion_platillos extends AppCompatActivity {

    private ArrayList<String> datos;
    String Key;
    int posicion;
    ArrayList<Platillos> arrayListPlatillos;
    @BindView( R.id.appbar_info )
    AppBarLayout appBarLayout;
    @BindView( R.id.collapsing_toolbar_info )
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView( R.id.toolbar_info )
    Toolbar toolbar_info;
    @BindView( R.id.image_view_info_heading )
    ImageView imageView_info;
    @BindView( R.id.text_view_info_restaurant )
    TextView text_view_restaurant;
    @BindView( R.id.text_view_info_platillo )
    TextView text_view_platillo;
    @BindView( R.id.text_view_info_precio )
    TextView text_view_precio;
    @BindView( R.id.text_view_info_direccion )
    TextView text_view_direccion;
    @BindView( R.id.recycler_view_info_platillos )
    RecyclerView Rview_info;
    RecyclerView.Adapter adapterRview;
    private RecyclerView.LayoutManager layourRview;
//    @Optional @BindView( R.id.recycler_view_info )
//    RecyclerView recyclerView_info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_informacion_platillos );
        ButterKnife.bind( this );
        if (Build.VERSION.SDK_INT > 16) {
            getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN );
        }
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        obtener_datos();
        cargarRecliclerView(mDatabase);
        configToolbar();
        cargarImage();
        cargarDatos_texto();
        layourRview = new LinearLayoutManager(this);
        Rview_info.setLayoutManager(layourRview);


    }

    private void cargarDatos_texto() {
        text_view_restaurant.setText( datos.get( 3 ) );
        text_view_precio.setText( datos.get( 1 ) );
        text_view_platillo.setText( datos.get( 2 ) );
        text_view_direccion.setText( datos.get( 4 ) );
    }
    private void PublicComment( Map<String,Object> datos){
        DatabaseReference coment= FirebaseDatabase.getInstance().getReference("Platillos").child( Key ).child( "Comentarios" );
        final DatabaseReference commentRef=coment.push();
        commentRef.setValue( datos).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(informacion_platillos.this,"Error: "+e ,Toast.LENGTH_SHORT ).show();
            }
        } ).addOnCompleteListener( new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(informacion_platillos.this,"Entro: "+task.getResult() ,Toast.LENGTH_SHORT ).show();
                }

            }
        } );


    }
    private void cargarRecliclerView(DatabaseReference mDatabase) {
        DatabaseReference coment= FirebaseDatabase.getInstance().getReference("Platillos").child( Key ).child( "Comentarios" );

        coment.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Comentarios> comentariosPlatillos= new ArrayList<>(  );
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Comentarios coment=snapshot.getValue(Comentarios.class);
                    comentariosPlatillos.add( coment );
                }
                Toast.makeText( informacion_platillos.this,"Completo",Toast.LENGTH_SHORT ).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
    }

    private void cargarImage() {
        byte[] decodedString = Base64.decode(datos.get( 0 ), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        imageView_info.setImageBitmap( decodedByte );
    }

    private void configToolbar() {
        setSupportActionBar( toolbar_info );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
    }

    public void obtener_datos(){
        
        
        try{
            //esto averiguar
            //Platillos platillos= (Platillos) getIntent().getSerializableExtra( "clase" );
            datos =getIntent().getExtras().getStringArrayList("lista");
            Key = getIntent().getExtras().getString("key");
	        posicion = getIntent().getExtras().getInt("posicion");
	        toolbar_info.setTitle( datos.get( 2 ) );
          //  ArrayList<Comentarios> arrayComentarios= (ArrayList<Comentarios>) getIntent().getSerializableExtra( "comentarios" );
        }catch (Exception e){
            Toast.makeText( this,"error en cargar datos", Toast.LENGTH_SHORT ).show();
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
