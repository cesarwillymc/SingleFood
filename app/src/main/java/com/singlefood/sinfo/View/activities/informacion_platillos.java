package com.singlefood.sinfo.View.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
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
import com.singlefood.sinfo.R;
import com.singlefood.sinfo.View.dialog.dialog_comentario_principal;
import com.singlefood.sinfo.utils.adapters.RecyclerComentariosAdapter;
import com.singlefood.sinfo.models.comentarios;
import com.singlefood.sinfo.models.platillos;
import com.singlefood.sinfo.models.usuariosSingle;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.annotations.NonNull;

public class informacion_platillos extends AppCompatActivity implements View.OnClickListener {
    private  GestureDetector gestureDetector;
    String Key;
    private RecyclerView.Adapter adapterRview;
    private ProgressDialog progressDialog;
    FirebaseAuth auth;
    FirebaseUser user;

    LinearLayout linearLayout;
    BottomSheetBehavior bottomSheetBehavior;
    @BindView( R.id.aic_appbar_info)
    AppBarLayout appBarLayout;
    @BindView( R.id.aic_collapsing_toolbar_info)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView( R.id.aic_toolbar_info)
    Toolbar toolbar_info;
    @BindView( R.id.aic_image_view_info_heading)
    ImageView imageView_info;
    @BindView( R.id.aic_fab_comment)
    FloatingActionButton btnfComment;
    @BindView(R.id.aic_fab_like)
    FloatingActionButton btnLike;
    @BindView( R.id.aic_button_comentar_collapse)
    Button button_coment_collapse;
    @BindView(R.id.aic_text_view_comentarios_total)
    TextView ComentariosTotal;
    //Button Sheep
    FloatingActionButton button_coment_hide;
    RatingBar rating_coment;
    EditText comentario_comment;
    Button publicar_commet;
    RecyclerView recyclerView_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.actividad_informacion_platillo);
        ButterKnife.bind( this );
        if (Build.VERSION.SDK_INT > 16) {
            getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN );
        }

        button_coment_hide=(FloatingActionButton) findViewById( R.id.aic_button_comentar_hide);
        button_coment_collapse.setOnClickListener( this );
        button_coment_hide.setOnClickListener( this );
        obtener_datos();
        getCommts();
        configToolbar();
        //cargarImage();
        linearLayout=(LinearLayout) findViewById( R.id.crear_comentario_layout_dialog ) ;
        bottomSheetBehavior= BottomSheetBehavior.from( linearLayout );
        recyclerView_info=(RecyclerView) findViewById( R.id.recycler_view_info_platillos ) ;
        LinearLayoutManager layoutManager= new LinearLayoutManager( this );
        RecyclerView.LayoutManager recycler_view_manager_info=layoutManager;
        recyclerView_info.setLayoutManager( recycler_view_manager_info );
        btnfComment.setOnClickListener(this);
        bottomSheetBehavior.setBottomSheetCallback( new BottomSheetBehavior.BottomSheetCallback() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onStateChanged(@androidx.annotation.NonNull View view, int i) {
                switch (i)
                {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        button_coment_hide.setVisibility( View.GONE );
                        button_coment_collapse.setVisibility( View.VISIBLE );
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        button_coment_hide.setVisibility( View.GONE );
                        button_coment_collapse.setVisibility( View.VISIBLE );
                        break;

                }
            }

            @Override
            public void onSlide(@androidx.annotation.NonNull View view, float v) {

            }
        } );
        //gestos
        gestos();

    }

    @SuppressLint("ClickableViewAccessibility")
    private void gestos() {


    }

    private ArrayList<comentarios> getUser(String Key) {
        DatabaseReference coment= FirebaseDatabase.getInstance().getReference("usuariosSingle").child( Key ).child( "comentarios" );
        ArrayList<comentarios> comentariosPlatillos= new ArrayList<>(  );
        coment.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    comentarios coment=snapshot.getValue(comentarios.class);
                    comentariosPlatillos.add( coment );
                }

            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {

            }
        } );
        return comentariosPlatillos;
    }
    private void PublicComment( Map<String,Object> datos){
        DatabaseReference coment= FirebaseDatabase.getInstance().getReference("platillos").child( Key ).child( "comentarios" );
        final DatabaseReference commentRef=coment.push();
        commentRef.setValue( datos).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(informacion_platillos.this,"Error: "+e ,Toast.LENGTH_SHORT ).show();
            }
        } ).addOnCompleteListener( new OnCompleteListener<Void>() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                   // Toast.makeText(informacion_platillos.this,"Entro: "+task.getResult() ,Toast.LENGTH_SHORT ).show();
                    bottomSheetBehavior.setState( BottomSheetBehavior.STATE_HIDDEN );
                    button_coment_collapse.setVisibility( View.VISIBLE );
                    button_coment_hide.setVisibility( View.GONE );
                    progressDialog.dismiss();
                    rating_coment.setRating( 0 );
                    comentario_comment.setText( "" );
                }

            }
        } );


    }
    public void obtener_datos(){
        try{
            Key = getIntent().getStringExtra("key");
        }catch (Exception e){
        }

    }

    private void getCommts() {
        DatabaseReference obtener=  FirebaseDatabase.getInstance().getReference("platillos").child( Key );
        obtener.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                ArrayList<platillos> arrayList= new ArrayList<>();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    platillos coment=dataSnapshot.getValue(platillos.class);
                    arrayList.add(coment);
                }
                ComentariosTotal.setText(arrayList.get(0).getComentarioscount()+"");
                toolbar_info.setTitle(arrayList.get(0).getNombrePlatillo() );
                cargarImage(arrayList.get(0).getImagenbase64());
                Bundle args = new Bundle();

                // Colocamos el String
                args.putString("key", Key);
                args.putString( "idUser", arrayList.get(0).getId_user() );
                args.putString( "tipo", arrayList.get(0).getTipo() );
                args.putString( "precio", arrayList.get(0).getPrecio() );
                args.putString( "direccion", arrayList.get(0).getDireccion() );

                // Supongamos que tu Fragment se llama TestFragment. Colocamos este nuevo Bundle como argumento en el fragmento.
                Fragment newFragment = new dialog_comentario_principal();
                newFragment.setArguments(args);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace( R.id.info_restaurant_frame,newFragment )
                        .commit();

            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference coment= FirebaseDatabase.getInstance().getReference("platillos").child( Key ).child( "comentarios" );

        coment.limitToFirst(10).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<comentarios> arrayListComentarios= new ArrayList<>();
                ArrayList<usuariosSingle> arrayList= new ArrayList<>();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    comentarios coment=snapshot.getValue(comentarios.class);
                    //arrayList.add(obtenerImagenUser(coment.getIdComentarios()));
                    arrayListComentarios.add( coment );
                }

                //ratingtex.setText( promedioRating(arrayListComentarios).toString());
                adapterRview = new RecyclerComentariosAdapter( informacion_platillos.this, R.layout.dialog_recycler_comentarios, arrayListComentarios, new RecyclerComentariosAdapter.OnItemClickListener2() {
                    @Override
                    public void OnClickListener2(comentarios comentarios, int adapterPosition) {
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

    private usuariosSingle obtenerImagenUser(String idComentarios) {
        return null ;
    }

    private void cargarImage(String base) {
        byte[] decodedString = Base64.decode(base, Base64.DEFAULT);
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
                finish();
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
                    Date date = new Date();
                    DateFormat hourFormat = new SimpleDateFormat("HH:mm:ss");
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    if(user != null){
                        Map<String,Object> map = new HashMap<>(  );
                        map.put( "idComentarios",user.getUid() );
                        map.put( "rating",rating_coment.getRating() );
                        map.put( "texto",comentario_comment.getText().toString().trim() );
                        map.put( "prioridad",0 );
                        map.put("hora",hourFormat.format(date));
                        map.put("fecha",dateFormat.format(date));
                        PublicComment( map );
                    }else{
                        progressDialog.dismiss();
                        Intent intent = new Intent( this, LoginActivity.class);
                        startActivity(intent);
                    }


                break;
            case R.id.aic_button_comentar_collapse:
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
            case R.id.aic_button_comentar_hide:
                bottomSheetBehavior.setState( BottomSheetBehavior.STATE_HIDDEN );

                button_coment_collapse.setVisibility( View.VISIBLE );
                button_coment_hide.setVisibility( View.GONE );
                break;
            case R.id.aic_fab_comment:
                button_coment_collapse.requestFocus();
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
