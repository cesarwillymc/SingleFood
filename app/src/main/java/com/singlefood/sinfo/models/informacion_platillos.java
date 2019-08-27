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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.singlefood.sinfo.R;
import com.singlefood.sinfo.models.productos.Platillos;

import java.util.ArrayList;

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

    private void cargarRecliclerView(DatabaseReference mDatabase) {

        mDatabase.child("Platillos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayListPlatillos= new ArrayList<>(  );
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){


///no modificar************************************************************
                    Platillos platillos= snapshot.getValue(Platillos.class);

                    arrayListPlatillos.add( platillos );
// hasta aca*****************************//nuevo adapter****************************************************


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
        datos =getIntent().getExtras().getStringArrayList("lista");
        Key = getIntent().getExtras().getString("key");
        posicion = getIntent().getExtras().getInt("posicion");
        toolbar_info.setTitle( datos.get( 2 ) );
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
