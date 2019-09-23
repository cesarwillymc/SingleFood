package com.singlefood.sinfo.models;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.singlefood.sinfo.LoginActivity;
import com.singlefood.sinfo.R;
import com.singlefood.sinfo.models.productos.Comentarios;
import com.singlefood.sinfo.models.productos.RecyclerComentariosAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.annotations.NonNull;

public class informacion_platillos extends AppCompatActivity implements View.OnClickListener{

    private ArrayList<String> datos;
    String Key;
    private RecyclerView.Adapter adapterRview;
    private ProgressDialog progressDialog;
    FirebaseAuth auth;
    FirebaseUser user;

    LinearLayout linearLayout;
    BottomSheetBehavior bottomSheetBehavior;
    @BindView( R.id.appbar_info )
    AppBarLayout appBarLayout;
    @BindView( R.id.collapsing_toolbar_info )
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView( R.id.toolbar_info )
    Toolbar toolbar_info;
    @BindView( R.id.image_view_info_heading )
    ImageView imageView_info;

    @BindView( R.id.button_comentar_collapse )
    Button button_coment_collapse;
    //Button Sheep
    FloatingActionButton button_coment_hide;
    RatingBar rating_coment;
    EditText comentario_comment;
    Button publicar_commet;
//    @BindView( R.id.button_comentar_hide )
//    Button button_coment_hide;
//    @BindView( R.id.crear_comentario_rating )
//    RatingBar rating_coment;
//    @BindView( R.id.crear_comentario_comentario )
//    EditText comentario_comment;
//    @BindView( R.id.crear_comentario_publicar )
//    Button publicar_commet;
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
        button_coment_hide=(FloatingActionButton) findViewById( R.id.button_comentar_hide );
        button_coment_collapse.setOnClickListener( this );
        button_coment_hide.setOnClickListener( this );
//        button_coment_hide.setOnClickListener( this );
//        publicar_commet.setOnClickListener( this );
        obtener_datos();
        getCommts();
        configToolbar();
        cargarImage();
        cargarDatos_texto();
        linearLayout=(LinearLayout) findViewById( R.id.crear_comentario_layout_dialog ) ;
        bottomSheetBehavior= BottomSheetBehavior.from( linearLayout );
        recyclerView_info=(RecyclerView) findViewById( R.id.recycler_view_info_platillos ) ;
        LinearLayoutManager layoutManager= new LinearLayoutManager( this );
        RecyclerView.LayoutManager recycler_view_manager_info=layoutManager;
        recyclerView_info.setLayoutManager( recycler_view_manager_info );

        btnfComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button_coment_collapse.requestFocus();
            }
        });
        bottomSheetBehavior.setBottomSheetCallback( new BottomSheetBehavior.BottomSheetCallback() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onStateChanged(@androidx.annotation.NonNull View view, int i) {
                switch (i)
                {
                    case BottomSheetBehavior.STATE_HIDDEN:
                       // button_coment_hide.setVisibility( View.GONE );
                        button_coment_collapse.setVisibility( View.VISIBLE );
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        //button_coment_hide.setVisibility( View.GONE );
                        button_coment_collapse.setVisibility( View.VISIBLE );
                        break;

                }
            }

            @Override
            public void onSlide(@androidx.annotation.NonNull View view, float v) {

            }
        } );

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
                    bottomSheetBehavior.setState( BottomSheetBehavior.STATE_HIDDEN );
                    button_coment_collapse.setVisibility( View.VISIBLE );
                    button_coment_hide.setVisibility( View.GONE );
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
                //ratingtex.setText( promedioRating(arrayListComentarios).toString());
                adapterRview = new RecyclerComentariosAdapter( informacion_platillos.this, R.layout.rv_comentarios, arrayListComentarios, new RecyclerComentariosAdapter.OnItemClickListener2() {
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
//        View view;
//        LayoutInflater inflater = (LayoutInflater)   getSystemService( Context.LAYOUT_INFLATER_SERVICE);
//        view = inflater.inflate(R.layout.include_info_restaurant, null);
//        text_view_restaurant.setText( datos.get( 3 ) );
//        text_view_precio.setText( datos.get( 1 ) );
//        text_view_platillo.setText( datos.get( 2 ) );
//        text_view_direccion.setText( datos.get( 4 ) );


    }
    public Float promedioRating(ArrayList<Comentarios> comentariosPlatillos){
        float temp=0;
        for (int i=0;i<comentariosPlatillos.size();i++){
            temp=temp+comentariosPlatillos.get(  i).getRating();
        }
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

    @SuppressLint("RestrictedApi")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.crear_comentario_publicar:
                    progressDialog=new ProgressDialog( this );
                    progressDialog.setCancelable( false );
                    progressDialog.setMessage( "Cargando tu comentarioo..." );
                    progressDialog.show();

                    if(user != null){
                        Map<String,Object> map = new HashMap<>(  );
                        map.put( "id_comentarios",user.getUid() );
                        map.put( "rating",rating_coment.getRating() );
                        map.put( "texto",comentario_comment.getText().toString().trim() );
                        PublicComment( map );
                    }else{
                        progressDialog.dismiss();
                        Intent intent = new Intent( this, LoginActivity.class);
                        startActivity(intent);
                    }


                break;
            case R.id.button_comentar_collapse:
                auth =FirebaseAuth.getInstance();
                user=auth.getCurrentUser();

                if(user != null){
                    cargar_bindeos();
                    bottomSheetBehavior.setState( BottomSheetBehavior.STATE_EXPANDED );

                    button_coment_hide.setVisibility( View.VISIBLE );
                    button_coment_collapse.setVisibility( View.GONE );
                }else{
                    Intent intent = new Intent( this, LoginActivity.class);
                    startActivity(intent);
                }

                break;
            case R.id.button_comentar_hide:
                bottomSheetBehavior.setState( BottomSheetBehavior.STATE_HIDDEN );

                button_coment_collapse.setVisibility( View.VISIBLE );
                button_coment_hide.setVisibility( View.GONE );
                break;
        }
    }

    private void cargar_bindeos() {

         rating_coment=(RatingBar) linearLayout.findViewById( R.id.crear_comentario_rating );
         comentario_comment=(EditText) linearLayout.findViewById( R.id.crear_comentario_comentario );
         publicar_commet=(Button) linearLayout.findViewById( R.id.crear_comentario_publicar );


         publicar_commet.setOnClickListener( this );
    }
}
