package com.singlefood.sinfo.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.singlefood.sinfo.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class informacion_platillos extends AppCompatActivity {

    private ArrayList<String> datos;
    @BindView( R.id.appbar_info )
    AppBarLayout appBarLayout;
    @BindView( R.id.collapsing_toolbar_info )
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView( R.id.toolbar_info )
    Toolbar toolbar_info;
    @BindView( R.id.image_view_info_heading )
    ImageView imageView_info;
//    @Optional @BindView( R.id.recycler_view_info )
//    RecyclerView recyclerView_info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_informacion_platillos );
        ButterKnife.bind( this );
        obtener_datos();
        configToolbar();
        cargarImage();
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        RecyclerView.LayoutManager layoutManager= linearLayoutManager;
//        recyclerView_info.setLayoutManager( layoutManager );


    }

    private void cargarImage() {
        byte[] decodedString = Base64.decode(datos.get( 0 ), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        imageView_info.setImageBitmap( decodedByte );
    }

    private void configToolbar() {
        setSupportActionBar( toolbar_info );
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        }
    }

    public void obtener_datos(){
        try{
            datos =getIntent().getExtras().getStringArrayList("lista");
            toolbar_info.setTitle( datos.get( 2 ) );
        }catch (Exception e){

        }


    }

}
