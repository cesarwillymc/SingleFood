package com.singlefood.sinfo.models;


import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.singlefood.sinfo.R;
import com.singlefood.sinfo.models.productos.Comentarios;
import com.singlefood.sinfo.models.productos.Platillos;
import com.singlefood.sinfo.models.productos.RecyclerProductoAdapter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class mapsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, ActivityCompat.OnRequestPermissionsResultCallback, View.OnClickListener, GoogleMap.OnMarkerClickListener {
    private View view;
    private MapView mapView;
    private Dialog dialog;
    private Uri fileImage;
    private Bitmap bitmap;
    private Marker m;
    private int TAKEFOTO=1;
    private RecyclerView.Adapter adapterRview;
    private RecyclerView.LayoutManager layourRview;
    private RecyclerProductoAdapter mAdapter;
    private RecyclerView Rview;
    private GoogleMap mMap;
    private ProgressDialog progressDialog;
    private FloatingActionButton fab;
    private List<Address> address;
    private List<String> nombres;
    private Geocoder geocoder;
    private StorageReference mStorageReference;
    private DatabaseReference mDatabase; //FIREBASE
    private FusedLocationProviderClient fusedLocationProviderClient; //Ultima Ubicacion
    private LocationRequest locationRequest; //Actualizar posicion
    private LocationCallback locationCallback; //ACtualizar posicion
    private Location location;
    //Dialog datos
    private EditText dialog_et_nombre;
    private EditText dialog_et_precio;
    private  Spinner dialog_spinner_tipo;
    private ImageView dialog_iv_foto;
    private RatingBar ratingBar_dialog;

    private ArrayList<Marker> tmpRealTimeMarkers = new ArrayList<>(); //Array Marcadores temporales de almacenamiento para hacer llamado
    private ArrayList<Marker> realTimeMarkers = new ArrayList<>();     //Marcadores tiempo real

    final ArrayList<Platillos> arrayListPlatillos= new ArrayList<>();
    ArrayList<Comentarios> arrayListComentarios= new ArrayList<>();

    final ArrayList<ArrayList<Comentarios>> arrayKeys= new ArrayList<>();
    final ArrayList<String> llaves= new ArrayList<>();

    //Declare HashMap to store mapping of marker to Activity
    final HashMap<String, String> markerMapPlatillos = new HashMap<String, String>();

    public mapsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate( R.layout.fragment_maps, container, false );
         fab = (FloatingActionButton) view.findViewById( R.id.floatingbar_save );
         fab.setOnClickListener(this );
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {

            new AlertDialog.Builder( getContext() )
                    .setTitle( "Activa Permiso" )
                    .setMessage( "Permiso desabilitado!! " )
                    .setPositiveButton( "Si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final Intent i = new Intent();
                            i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            i.addCategory(Intent.CATEGORY_DEFAULT);
                            i.setData(Uri.parse("package:" + getContext().getPackageName()));
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                            getContext().startActivity(i);
                        }
                    } )
                    .setNegativeButton( "No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            if (Build.VERSION.SDK_INT >= 26) {
                                ft.setReorderingAllowed(false);
                            }
                            ft.detach(mapsFragment.this).attach(mapsFragment.this).commit();
                        }
                    } )
                    .show();
        }

        return view;
    }
    private List<String> getAllnames(){
        return new ArrayList<String>(){{
            add("Elias");
            add("Antonio");
            add("Cesar");
            add("Willy");
        }};
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated( view, savedInstanceState );

        mapView = (MapView) view.findViewById( R.id.map );
        if (mapView != null) {
            mapView.onCreate( null );
            mapView.onResume();
            mapView.getMapAsync( this );
        }
        Rview = view.findViewById(R.id.my_recycler_view);
//        this.nombres = getAllnames();
        layourRview = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        Rview.setLayoutManager(layourRview);

        mStorageReference= FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference(); //Instanciar BD Firebase
        initFused();
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    private void initFused() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient( getContext() );
        if (ActivityCompat.checkSelfPermission( getContext(), Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( getContext(), Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        buildLocationRequest();
        buildLocationCallBack();
        fusedLocationProviderClient.requestLocationUpdates( locationRequest, locationCallback, Looper.myLooper() );
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission( getContext(), Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( getContext(), Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled( true );
        mMap.getUiSettings().setCompassEnabled( false );
        mMap.getUiSettings().setIndoorLevelPickerEnabled( false );
        mMap.setOnMarkerClickListener(this);


        mDatabase.child("Platillos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(Marker marker:realTimeMarkers){
                    marker.remove();
                }

                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Platillos platillos= snapshot.getValue(Platillos.class);
                    Double latitud = platillos.getPlaces().getLatitud();
                    Double longitud = platillos.getPlaces().getLongitud();

                    Marker mUbicacionPlatillo = mMap.addMarker(new MarkerOptions().position(new LatLng(latitud, longitud)));
                    String idMarker = mUbicacionPlatillo.getId();
                    markerMapPlatillos.put(idMarker, platillos.getNombrePlatillo());


                    llaves.add( snapshot.getKey() );
                    arrayListPlatillos.add( platillos );
                    arrayKeys.add( getCommts( snapshot.getKey() ) );
                }


                adapterRview = new RecyclerProductoAdapter(getContext(), R.layout.rv_comentarios_items, arrayListPlatillos,arrayKeys, new RecyclerProductoAdapter.OnItemClickListener() {
                    @Override
                    public void OnClickListener(Platillos platillos, ArrayList<Comentarios> arrayComentarios, int position) {

                        Intent i = new Intent(getActivity(), informacion_platillos.class);
                        ArrayList<String> lista = new ArrayList<>(  );
                        lista.add( arrayListPlatillos.get( position ).getImagenbase64() );
                        lista.add( arrayListPlatillos.get( position ).getPrecio() );
                        lista.add( arrayListPlatillos.get( position ).getNombrePlatillo() );
                        lista.add( arrayListPlatillos.get( position ).getTipo() );
                        lista.add( arrayListPlatillos.get( position ).getPlaces().getDireccion() );
                        lista.add( arrayListPlatillos.get( position ).getPlaces().getCiudad() );
                        lista.add( arrayListPlatillos.get( position ).getPlaces().getIdUser() );
                        i.putStringArrayListExtra( "lista",lista );
                        i.putExtra( "key",llaves.get( position ) );

                        startActivity(i);

                    }
                } );

                Rview.setAdapter(adapterRview);

                realTimeMarkers.clear();
                realTimeMarkers.addAll(tmpRealTimeMarkers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        buildLocationRequest();
        buildLocationCallBack();
    }
    private ArrayList<Comentarios> getCommts(String Key) {
        DatabaseReference coment= FirebaseDatabase.getInstance().getReference("Platillos").child( Key ).child( "Comentarios" );
        ArrayList<Comentarios> comentariosPlatillos= new ArrayList<>(  );
        coment.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Comentarios coment=snapshot.getValue(Comentarios.class);
                    comentariosPlatillos.add( coment );
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
        return comentariosPlatillos;
    }

    private void buildLocationCallBack(){

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                location = locationResult.getLocations().get(locationResult.getLocations().size() - 1);
                Log.e("Location", "" + location.getLatitude() + "" + location.getLongitude());

                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        //OBTENER ULTIMA UBICACION DEL DISPOSITIVO
                        if (location != null) {


                        }
                         mMap.animateCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()),15.0f));
                        try {

                            geocoder= new Geocoder( getContext(), Locale.getDefault() );
                            address= geocoder.getFromLocation( location.getLatitude(),location.getLongitude(),1 );
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

        };
    }
    private void buildLocationRequest(){
        locationRequest = new LocationRequest();
        locationRequest.setPriority( LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10);
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.floatingbar_save:
                cargarProducto( "Hola",15.2 );
                break;
            case  R.id.dialog_yes:
                progressDialog=new ProgressDialog( getContext());
                progressDialog.setMessage( "Compartiendo tu recomendación" );
                progressDialog.setCancelable( false );
                progressDialog.show();

                String platillo=dialog_et_nombre.getText().toString().trim();
                String precio=dialog_et_precio.getText().toString().trim();

                if (platillo.equals( "" ) && precio.equals( "" )&&ratingBar_dialog.getRating()==0){
                    progressDialog.dismiss();
                    Toast.makeText( getContext(),"Datos faltan llenar+"+platillo+"+"+precio , Toast.LENGTH_SHORT ).show();
                }else {
                    guardarDatosFirebaseDialogNotDatabase(platillo,precio );
                }

                break;
            case  R.id.dialog_no:
                dialog.dismiss();
                Toast.makeText( getContext(),"Datos No Guardados", Toast.LENGTH_SHORT ).show();
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
        base_datos.put( "ciudad", address.get( 0 ).getAddressLine( 0 ) );
        base_datos.put( "direccion", address.get( 0 ).getLocality() );
        base_datos.put( "latitud", address.get( 0 ).getLatitude() );
        base_datos.put( "longitud", address.get( 0 ).getLongitude() );
        base_datos.put( "id_user", "prueba001" );
        comentarios.put( "id_comentarios","prueba001" );
        comentarios.put( "texto","Hola mundo muy rica Comida" );
        comentarios.put( "rating",ratingBar_dialog.getRating() );


        if(bitmap!=null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            //encode
            datos.put( "nombrePlatillo", platillo );
            datos.put( "precio", precio );
            datos.put( "tipo", dialog_spinner_tipo.getSelectedItem().toString() );
            datos.put( "imagenbase64", imageString );
            datos.put( "places",base_datos);
           // datos.put( "comentarios_platillo",comentarios );

            DatabaseReference coment= FirebaseDatabase.getInstance().getReference("Platillos").push();
            coment.setValue( datos );
            coment.child( "Comentarios" ).push().setValue( comentarios ).addOnCompleteListener( new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    dialog.dismiss();
                    progressDialog.dismiss();
                }
            } ).addOnFailureListener( new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText( getContext(),"Error al Guardar", Toast.LENGTH_SHORT ).show();
                    progressDialog.dismiss();
                }
            } );
//            mDatabase.child( "Platillos" ).push().setValue( datos ).addOnCompleteListener( new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//
//                    dialog.dismiss();
//                    progressDialog.dismiss();
//                }
//            } ).addOnFailureListener( new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Toast.makeText( getContext(),"Error al Guardar", Toast.LENGTH_SHORT ).show();
//                    progressDialog.dismiss();
//                }
//            } );
        }else {
            progressDialog.dismiss();
            Toast.makeText( getContext(),"Error Bitmap vacio",Toast.LENGTH_SHORT ).show();
        }
    }

    public  void cargarProducto(String nombre,Double precio){
        dialog =new Dialog( getContext() );
        dialog.setContentView( R.layout.dialog_eow );
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable( Color.TRANSPARENT ) );
        //findviewid
        dialog_et_nombre= dialog.findViewById( R.id.dialog_edit_text_nombre ) ;
        dialog_et_precio= dialog.findViewById( R.id.dialog_edit_text_precio ) ;
        dialog_spinner_tipo= dialog.findViewById( R.id.dialog_spinner ) ;
        dialog_iv_foto= dialog.findViewById( R.id.dialog_imageView );
        Button dialogButtonsi= dialog.findViewById( R.id.dialog_yes );
        Button dialogButtonno= dialog.findViewById( R.id.dialog_no );
        TextView dialogTextDireccion=dialog.findViewById( R.id.dialog_text_view_direccion );
        TextView dialogTextCiudad=dialog.findViewById( R.id.dialog_text_view_ciudad );
        ratingBar_dialog=dialog.findViewById( R.id.dialog_rating_bar );
        //on click
        dialog_iv_foto.setOnClickListener( this );
        dialogButtonsi.setOnClickListener( this );
        dialogButtonno.setOnClickListener( this );
//      esto es de adrees
        String adress=address.get( 0 ).getAddressLine( 0 ) ;
        String city=address.get( 0 ).getLocality(  ) ;
        double latitud=address.get( 0 ).getLatitude() ;
        double longitud=address.get( 0 ).getLongitude(  ) ;
        //dar valor a los tv
        dialogTextCiudad.setText( adress );
        dialogTextDireccion.setText( city );

        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,@Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        if (requestCode==TAKEFOTO&&resultCode==RESULT_OK&&data!=null){

            bitmap=(Bitmap) data.getExtras().get( "data" );

            dialog_iv_foto.setImageBitmap( bitmap );
            fileImage = data.getData();
        }else {
            Toast.makeText( getContext(),"Error de data no se obtuvo" ,Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Method: onMarkerClick
     * Detail: Se ejecuta cuando hay un click sobre un marcador del puntero de GoogleMaps
     * */

    @Override
    public boolean onMarkerClick(Marker marker) {

        String strNombrePlato = markerMapPlatillos.get(marker.getId()).toString();
        Intent i = new Intent(getActivity(), informacion_platillos.class);

        ArrayList<String> lista = new ArrayList<>(  );
        for(int position = 0; position < arrayListPlatillos.size(); position++){
            if(strNombrePlato.equals(arrayListPlatillos.get( position ).getNombrePlatillo())){
                lista.add( arrayListPlatillos.get( position ).getImagenbase64() );
                lista.add( arrayListPlatillos.get( position ).getPrecio() );
                lista.add( arrayListPlatillos.get( position ).getNombrePlatillo() );
                lista.add( arrayListPlatillos.get( position ).getTipo() );
                lista.add( arrayListPlatillos.get( position ).getPlaces().getDireccion() );
                lista.add( arrayListPlatillos.get( position ).getPlaces().getCiudad() );
                lista.add( arrayListPlatillos.get( position ).getPlaces().getIdUser() );
                i.putStringArrayListExtra( "lista",lista );
                i.putExtra( "key",llaves.get( position ) );

                startActivity(i);
                break;
            }

        }


        return false;
    }
}
