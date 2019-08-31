package com.singlefood.sinfo.models;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.singlefood.sinfo.R;
import com.singlefood.sinfo.models.productos.Comentarios;
import com.singlefood.sinfo.models.productos.RecyclerComentariosAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class informacion_platillos extends AppCompatActivity implements View.OnClickListener{

    private ArrayList<String> datos;
    String Key;
    private RecyclerView.Adapter adapterRview;
    private ProgressDialog progressDialog;
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
    @BindView( R.id.rating_bar_info_platillos )
    RatingBar ratingBar;
    @BindView( R.id.button_informacion_platillos )
    Button button_coment;
    @BindView( R.id.edit_text_informacion_platillos )
    EditText editText_coment;
    @BindView( R.id.rating_bar_informacion_platillos )
    RatingBar rating_coment;
    @BindView( R.id.btnfComment )
    FloatingActionButton btnfComment;

    RecyclerView recyclerView_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_informacion_platillos );
        ButterKnife.bind( this );
        if (Build.VERSION.SDK_INT > 16) {
            getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN );
        }
        button_coment.setOnClickListener( this );
        obtener_datos();
        getCommts();
        configToolbar();
        cargarImage();
        cargarDatos_texto();
        recyclerView_info=(RecyclerView) findViewById( R.id.recycler_view_info_platillos ) ;
        LinearLayoutManager layoutManager= new LinearLayoutManager( this );
        RecyclerView.LayoutManager recycler_view_manager_info=layoutManager;
        recyclerView_info.setLayoutManager( recycler_view_manager_info );

        btnfComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText_coment.requestFocus();
            }
        });

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
                   // Toast.makeText(informacion_platillos.this,"Entro: "+task.getResult() ,Toast.LENGTH_SHORT ).show();
                    progressDialog.dismiss();
                }

            }
        } );


    }
    public void obtener_datos(){
        try{
            datos =getIntent().getStringArrayListExtra(  "lista");
            Key = getIntent().getStringExtra("key");
            toolbar_info.setTitle( datos.get( 2 ) );

            //  ArrayList<Comentarios> arrayComentarios= (ArrayList<Comentarios>) getIntent().getSerializableExtra( "comentarios" );
        }catch (Exception e){
        }

    }

    private void getCommts() {
        DatabaseReference coment= FirebaseDatabase.getInstance().getReference("Platillos").child( Key ).child( "Comentarios" );

        coment.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Comentarios> arrayListComentarios= new ArrayList<>();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Comentarios coment=snapshot.getValue(Comentarios.class);
                    arrayListComentarios.add( coment );
                }
                ratingBar.setRating( promedioRating(arrayListComentarios));
                //Toast.makeText( informacion_platillos.this,"key: "+ comentariosPlatillos.get( 0 ).getTexto(),Toast.LENGTH_SHORT ).show();
                //Toast.makeText( informacion_platillos.this,"Prueba: "+comentariosPlatillos.size(),Toast.LENGTH_SHORT ).show();
                adapterRview = new RecyclerComentariosAdapter( informacion_platillos.this, R.layout.rv_comentarios_items, arrayListComentarios, new RecyclerComentariosAdapter.OnItemClickListener2() {
                    @Override
                    public void OnClickListener2(Comentarios comentarios, int adapterPosition) {
                        Toast.makeText( informacion_platillos.this,"Prueba: "+comentarios.getTexto(),Toast.LENGTH_SHORT ).show();

                    }
                } );

                recyclerView_info.setAdapter(adapterRview);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
    }
    private void cargarDatos_texto() {
        text_view_restaurant.setText( datos.get( 3 ) );
        text_view_precio.setText( datos.get( 1 ) );
        text_view_platillo.setText( datos.get( 2 ) );
        text_view_direccion.setText( datos.get( 4 ) );


    }
    public Float promedioRating(ArrayList<Comentarios> comentariosPlatillos){
        float temp=0;
       // Toast.makeText( this,"Tamanñ array: "+ comentariosPlatillos.size(),Toast.LENGTH_SHORT ).show();
        for (int i=0;i<comentariosPlatillos.size();i++){
            temp=temp+comentariosPlatillos.get(  i).getRating();
            //Toast.makeText( this,"rating: "+ temp,Toast.LENGTH_SHORT ).show();
        }
       //Toast.makeText( this,"rating complete: "+ temp,Toast.LENGTH_SHORT ).show();
        return temp/comentariosPlatillos.size();

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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_informacion_platillos:
                    progressDialog=new ProgressDialog( this );
                    progressDialog.setCancelable( false );
                    progressDialog.setMessage( "Cargando tu comentarioo..." );
                    progressDialog.show();
                    Map<String,Object> map = new HashMap<>(  );
                    map.put( "id_comentarios","Prueba001" );
                    map.put( "rating",rating_coment.getRating() );
                    map.put( "texto",editText_coment.getText().toString().trim() );
                    PublicComment( map );

                break;
        }
    }
}
