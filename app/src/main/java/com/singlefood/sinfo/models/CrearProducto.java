package com.singlefood.sinfo.models;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.singlefood.sinfo.R;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CrearProducto extends AppCompatActivity implements View.OnClickListener {
    private int TAKEFOTO=1;
    Bitmap bitmap;
    ProgressDialog progressDialog;
    @BindView( R.id.dialog_edit_text_comentario )
    EditText  Comentario;
    @BindView( R.id.dialog_edit_text_nombre )
    EditText  nombre;
    @BindView( R.id.dialog_edit_text_precio )
    EditText  precioplatillo;
    @BindView( R.id.dialog_imageView )
    ImageView imagen;
    @BindView( R.id.dialog_rating_bar )
    RatingBar ratingBar;
    @BindView( R.id.dialog_spinner )
    Spinner spinner;
    @BindView( R.id.dialog_yes )
    Button yes;
    @BindView( R.id.dialog_no )
    Button  no;
    @BindView( R.id.dialog_text_view_ciudad )
    TextView ciudad;
    @BindView( R.id.dialog_text_view_direccion )
    TextView  direccion;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_crear_producto );
        ButterKnife.bind( this );
        yes.setOnClickListener( this );
        no.setOnClickListener( this );
        imagen.setOnClickListener( this );

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.dialog_yes:
                progressDialog=new ProgressDialog( this);
                progressDialog.setMessage( "Compartiendo tu recomendación" );
                progressDialog.setCancelable( false );
                progressDialog.show();

                String platillo=nombre.getText().toString().trim();
                String precio= precioplatillo.getText().toString().trim();

                if (platillo.equals( "" ) && precio.equals( "" )&&ratingBar.getRating()==0){
                    progressDialog.dismiss();
                    Toast.makeText( this,"Datos faltan llenar+"+platillo+"+"+precio , Toast.LENGTH_SHORT ).show();
                }else {
                    guardarDatosFirebaseDialogNotDatabase(platillo,precio );
                    finish();
                }
                break;
            case R.id.dialog_no:
                finish();
                break;
            case R.id.dialog_imageView:
                Intent intent= new Intent( );
                intent.setAction(  MediaStore.ACTION_IMAGE_CAPTURE );
                startActivityForResult( intent,TAKEFOTO );
                break;
        }
    }
    private void guardarDatosFirebaseDialogNotDatabase(String platillo, String precio)  {
        final Map<String,Object> datos=new HashMap<>(  );
        final Map<String,Object> base_datos=new HashMap<>(  );
        final Map<String,Object> comentarios=new HashMap<>(  );
       // base_datos.put( "latitud", address.get( 0 ).getLatitude() );
       // base_datos.put( "longitud", address.get( 0 ).getLongitude() );
        base_datos.put( "id_user", "prueba001" );
        comentarios.put( "id_comentarios","prueba001" );
        comentarios.put( "texto",this.Comentario.getText().toString());
        comentarios.put( "rating",ratingBar.getRating() );


        if(bitmap!=null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            //encode
            datos.put( "nombrePlatillo", platillo );
            datos.put( "precio", precio );
            datos.put( "tipo", spinner.getSelectedItem().toString() );
            datos.put( "imagenbase64", imageString );
            datos.put( "places",base_datos);

            DatabaseReference coment= FirebaseDatabase.getInstance().getReference("Platillos").push();
            coment.setValue( datos );
            coment.child( "Comentarios" ).push().setValue( comentarios ).addOnCompleteListener( new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progressDialog.dismiss();
                }
            } ).addOnFailureListener( new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText( CrearProducto.this,"Error al Guardar", Toast.LENGTH_SHORT ).show();
                    progressDialog.dismiss();
                }
            } );
        }else {
            progressDialog.dismiss();
            Toast.makeText(    CrearProducto.this,"Error Bitmap vacio",Toast.LENGTH_SHORT ).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,@Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        if (requestCode==TAKEFOTO&&resultCode==RESULT_OK&&data!=null){

            bitmap=(Bitmap) data.getExtras().get( "data" );

            imagen.setImageBitmap( bitmap );
        }else {
            Toast.makeText( this,"Error de data no se obtuvo" ,Toast.LENGTH_SHORT).show();
        }
    }
}
