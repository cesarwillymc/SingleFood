package com.singlefood.sinfo.models;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
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
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.singlefood.sinfo.LoginActivity;
import com.singlefood.sinfo.R;
import com.singlefood.sinfo.models.productos.RecyclerProductoAdapter;
import com.singlefood.sinfo.models.productos.comentarios;
import com.singlefood.sinfo.models.productos.platillos;
import com.singlefood.sinfo.utils.Constants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class fragMapaPrincipal extends Fragment implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, ActivityCompat.OnRequestPermissionsResultCallback, View.OnClickListener, GoogleMap.OnMarkerClickListener {
    private View view;
    private MapView mapView;
    private Uri fileImage;
    private Bitmap bitmap;
    private Marker m;
    private final int TAKEFOTO=1;
    private final int GPS=51;
    private RecyclerView.Adapter adapterRVTarjetaPlatillo;
    private RecyclerView.LayoutManager layourRview;
    private RecyclerProductoAdapter mAdapter;
    private RecyclerView rvListaPlatillos;
    private GoogleMap mMap;
    private ProgressDialog progressDialog;
    private FloatingActionButton fab_collapse;
    private FloatingActionButton fab_hidden;
    private List<Address> address;
    private List<String> nombres;
    private Geocoder geocoder;
    private StorageReference mStorageReference;
    private DatabaseReference mDatabase; //FIREBASE
    private FusedLocationProviderClient fusedLocationProviderClient; //Ultima Ubicacion
    private LocationRequest locationRequest; //Actualizar posicion
    private LocationCallback locationCallback; //ACtualizar posicion
    private Location location;
    private List<AutocompletePrediction> predictionList;
    //Dialog datos
    Button dialogButtonsi;
    private AutoCompleteTextView acPlatillo;
    private AutoCompleteTextView acBuscadorPlatillo;
    private EditText dialog_et_precio;
    private EditText dialog_et_direccion;
    private ImageView dialog_iv_foto;
    private RatingBar ratingBar_dialog;
    private SearchView searchView;
    private EditText dialog_comentario;
    private ArrayList<Marker> tmpRealTimeMarkers = new ArrayList<>(); //Array Marcadores temporales de almacenamiento para hacer llamado
    private ArrayList<Marker> realTimeMarkers = new ArrayList<>();     //Marcadores tiempo real
//fabbbbbbbbbbbbbbbbbbbbbb
    LinearLayout linearLayout;
    BottomSheetBehavior bottomSheetBehavior;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    final HashMap<String, String> markerMapPlatillos = new HashMap<String, String>();
    ArrayList<platillos> arrayListPlatillos= new ArrayList<>();
    private Context context;
    public fragMapaPrincipal() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate( R.layout.fra_mapa_principal, container, false );
        linearLayout=(LinearLayout) view.findViewById( R.id.crear_comida_dialog ) ;
        bottomSheetBehavior= BottomSheetBehavior.from( linearLayout );
        fab_collapse = (FloatingActionButton) view.findViewById( R.id.fab_collapse_dialog );
        fab_hidden = (FloatingActionButton) view.findViewById( R.id.fab_hidden_dialog );



        fab_collapse.setOnClickListener(this );
        fab_hidden.setOnClickListener( this );
        context = getContext();
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
                            ft.detach(fragMapaPrincipal.this).attach(fragMapaPrincipal.this).commit();
                        }
                    } )
                    .show();
        }
        bottomSheetBehavior.setBottomSheetCallback( new BottomSheetBehavior.BottomSheetCallback() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                switch (i)
                {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        fab_hidden.setVisibility( View.GONE );
                        fab_collapse.setVisibility( View.VISIBLE );
                        break;
                        case BottomSheetBehavior.STATE_COLLAPSED:
                            fab_hidden.setVisibility( View.GONE );
                            fab_collapse.setVisibility( View.VISIBLE );
                            break;

                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        } );


        //Buscador de platos en la parte superior
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>
                (getContext(), android.R.layout.select_dialog_item, Constants.foods);

        acBuscadorPlatillo = view.findViewById(R.id.acBuscarPlatillos);
        acBuscadorPlatillo.setThreshold(1);//num de caracteres para iniciar el autocompletado
        acBuscadorPlatillo.setAdapter(adapter);
        acBuscadorPlatillo.setTextColor(Color.GREEN);

        acBuscadorPlatillo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        acBuscadorPlatillo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> p, View v, int pos, long id) {
                InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(acBuscadorPlatillo.getWindowToken(), 0);

                mMap.animateCamera( CameraUpdateFactory.newLatLngZoom( new LatLng( location.getLatitude(),location.getLongitude()),14 ));
                Toast.makeText( getContext(),"Disfrute de estas delicias!!" , Toast.LENGTH_SHORT ).show();
            }
        });

        return view;
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
        //GPS ENABLE
        gpsEnable();

        rvListaPlatillos = view.findViewById(R.id.rvListaPlatillos);
        layourRview = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvListaPlatillos.setLayoutManager(layourRview);

        mStorageReference= FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference(); //Instanciar BD Firebase


    }

    private void gpsEnable() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient( getContext() );
        Places.initialize( getContext(),"AIzaSyAgSv7wL2PTgdXSiKggKstMiiPYT-87zb4" );
        PlacesClient placesClient= Places.createClient( getContext() );
        AutocompleteSessionToken token= AutocompleteSessionToken.newInstance();
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval( 10000 );
        locationRequest.setFastestInterval( 5000 );
        locationRequest.setPriority( locationRequest.PRIORITY_HIGH_ACCURACY );
        LocationSettingsRequest.Builder builder= new LocationSettingsRequest.Builder().addLocationRequest( locationRequest );
        SettingsClient settingsClient =LocationServices.getSettingsClient( getActivity() );
        Task<LocationSettingsResponse> task= settingsClient.checkLocationSettings( builder.build() );
        task.addOnSuccessListener( new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getDeviceLocation();
            }
        } );
        task.addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(e instanceof ResolvableApiException){
                    ResolvableApiException resolvableApiException= (ResolvableApiException) e;
                    try {
                        resolvableApiException.startResolutionForResult( getActivity(),GPS );
                    } catch (IntentSender.SendIntentException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        } );
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMyLocationEnabled( true );
        mMap.getUiSettings().setMyLocationButtonEnabled( true );
        mMap.getUiSettings().setCompassEnabled( false );
        mMap.getUiSettings().setIndoorLevelPickerEnabled( false );
        mMap.setOnMarkerClickListener(this);

        if(mapView!=null){
            View locationButton=((View) mapView.findViewById( Integer.parseInt( "1" ) ).getParent()).findViewById( Integer.parseInt( "2" ) );
            RelativeLayout.LayoutParams layoutParams=(RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,RelativeLayout.TRUE );
            layoutParams.addRule( RelativeLayout.ALIGN_PARENT_BOTTOM,0  );
            layoutParams.setMargins( 0,750,400,100  );
        }

        mDatabase.child("platillos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(Marker marker:realTimeMarkers){
                    marker.remove();
                }
                ArrayList<String> llaves= new ArrayList<>();

                 ArrayList<ArrayList<comentarios>> arrayKeys= new ArrayList<>();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    platillos platillosc= snapshot.getValue( platillos.class);
                    Double latitud = platillosc.getPlaces().getLatitud();
                    Double longitud = platillosc.getPlaces().getLongitud();

                    Marker mUbicacionPlatillo = mMap.addMarker(new MarkerOptions().
                                                                    position(
                                                                            new LatLng(latitud, longitud))
                                                                    .icon(
                                                                            BitmapDescriptorFactory.fromResource(
                                                                                    R.drawable.ico_marker_meat))
                                                                );
                    String idMarker = mUbicacionPlatillo.getId();
                    markerMapPlatillos.put(idMarker, platillosc.getNombrePlatillo());

                    llaves.add( snapshot.getKey() );
                    arrayKeys.add( getCommts( snapshot.getKey() ) );
                    arrayListPlatillos.add( platillosc );

                }


                adapterRVTarjetaPlatillo = new RecyclerProductoAdapter(getContext(), R.layout.rv_tarjeta_platillo, arrayListPlatillos,arrayKeys, new RecyclerProductoAdapter.OnItemClickListener() {
                    @Override
                    public void OnClickListener(platillos platillos, ArrayList<comentarios> arrayComentarios, int position) {

                        Intent i = new Intent(getActivity(), informacion_platillos.class);
                        ArrayList<String> lista = new ArrayList<>(  );
                        lista.add( arrayListPlatillos.get( position ).getImagenbase64() );
                        lista.add( arrayListPlatillos.get( position ).getNombrePlatillo() );
                        lista.add( arrayListPlatillos.get( position ).getId_user() );
                        lista.add( arrayListPlatillos.get( position ).getTipo() );
                        lista.add( arrayListPlatillos.get( position ).getPrecio() );
                        lista.add( arrayListPlatillos.get( position ).getDireccion() );
                        i.putStringArrayListExtra( "lista",lista );
                        i.putExtra( "key",llaves.get( position ) );

                        startActivity(i);

                    }
                } );

                rvListaPlatillos.setAdapter(adapterRVTarjetaPlatillo);

                realTimeMarkers.clear();
                realTimeMarkers.addAll(tmpRealTimeMarkers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private ArrayList<comentarios> getCommts(String Key) {
        DatabaseReference coment= FirebaseDatabase.getInstance().getReference("platillos").child( Key ).child( "comentarios" );
        ArrayList<comentarios> comentariosPlatillos= new ArrayList<>(  );
        coment.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    comentarios coment=snapshot.getValue(comentarios.class);
                    comentariosPlatillos.add( coment );
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
        return comentariosPlatillos;
    }


    @Override
    public boolean onMyLocationButtonClick() {

        return false;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.fab_collapse_dialog:

                FirebaseAuth mAuth= FirebaseAuth.getInstance();
                FirebaseUser user=mAuth.getCurrentUser();
                if(user != null){
                    crearPlatillo();
                    bottomSheetBehavior.setState( BottomSheetBehavior.STATE_HALF_EXPANDED );
                    fab_hidden.setVisibility( View.VISIBLE );
                    fab_collapse.setVisibility( View.GONE );
                }else{
                    Intent intent = new Intent( getContext(), LoginActivity.class);
                    startActivity(intent);
                }


                break;
            case R.id.fab_hidden_dialog:
                bottomSheetBehavior.setState( BottomSheetBehavior.STATE_HIDDEN );
                fab_hidden.setVisibility( View.GONE );
                fab_collapse.setVisibility( View.VISIBLE );
                break;
            case  R.id.dialog_yes:
                progressDialog=new ProgressDialog( getContext());
                progressDialog.setMessage( "Compartiendo tu recomendación" );
                progressDialog.setCancelable( false );
                progressDialog.show();

                String platillo=acPlatillo.getText().toString().trim();
                String precio=dialog_et_precio.getText().toString().trim();

                if (platillo.equals( "" ) && precio.equals( "" )&&ratingBar_dialog.getRating()==0){
                    progressDialog.dismiss();
                    Toast.makeText( getContext(),"Datos faltan llenar+"+platillo+"+"+precio , Toast.LENGTH_SHORT ).show();
                }else {
                    guardarDatosFirebaseDialogNotDatabase(platillo,precio );
                }

                break;
            case R.id.dialog_imageView:
                Intent intent= new Intent( );
                intent.setAction(  MediaStore.ACTION_IMAGE_CAPTURE );
                startActivityForResult( intent,TAKEFOTO );
                //openCameraIntent();
                break;

        }
    }

    private void guardarDatosFirebaseDialogNotDatabase(String platillo, String precio)  {
        final Map<String,Object> datos=new HashMap<>(  );
        final Map<String,Object> base_datos=new HashMap<>(  );
        final Map<String,Object> comentarios=new HashMap<>(  );

        Date date = new Date();
        DateFormat hourFormat = new SimpleDateFormat("HH:mm:ss");
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        FirebaseAuth mauth=FirebaseAuth.getInstance();
        FirebaseUser user = mauth.getCurrentUser();

        base_datos.put( "latitud", address.get( 0 ).getLatitude() );
        base_datos.put( "longitud", address.get( 0 ).getLongitude() );
        base_datos.put( "id_user", user.getUid() );
        comentarios.put( "id_comentarios",user.getUid() );
        comentarios.put( "texto",dialog_comentario.getText().toString() );
        comentarios.put( "rating",ratingBar_dialog.getRating() );
        comentarios.put( "prioridad",1);
        comentarios.put("hora",hourFormat.format(date));
        comentarios.put("fecha",dateFormat.format(date));

        if(bitmap!=null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            byte[] imageBytes = baos.toByteArray();
            String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            //encode
            datos.put( "nombrePlatillo", platillo );
            datos.put( "precio", precio );
            datos.put( "direccion", dialog_et_direccion.getText().toString()  );
            datos.put( "tipo", "general" );
            datos.put( "imagenbase64", imageString );
            datos.put( "places",base_datos);
            datos.put("id_user",user.getUid());
           // datos.put( "comentarios_platillo",comentarios );

            DatabaseReference coment= FirebaseDatabase.getInstance().getReference("platillos").push();
            coment.setValue( datos );
            coment.child( "comentarios" ).push().setValue( comentarios ).addOnCompleteListener( new OnCompleteListener<Void>() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    //UploadImage();
                    progressDialog.dismiss();
                    bottomSheetBehavior.setState( BottomSheetBehavior.STATE_HIDDEN );
                    fab_hidden.setVisibility( View.GONE );
                    fab_collapse.setVisibility( View.VISIBLE );
                    ratingBar_dialog.setRating( 0 );
                    dialog_iv_foto.setImageBitmap( null );
                    acPlatillo.setText( "" );
                    dialog_et_precio.setText( "" );
                    dialog_comentario.setText( "" );

                }
            } ).addOnFailureListener( new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText( getContext(),"Error al Guardar", Toast.LENGTH_SHORT ).show();
                    progressDialog.dismiss();
                }
            } );
        }else {
            progressDialog.dismiss();
            Toast.makeText( getContext(),"Error Bitmap vacio",Toast.LENGTH_SHORT ).show();
        }
    }

    /**
     * cargarProducto: se ejecuta para mostrar un modal y solicitar datos para registrar un platillo
     * */
    public void crearPlatillo(){
        dialog_et_precio= view.findViewById( R.id.dialog_edit_text_precio ) ;
        dialog_et_direccion= view.findViewById( R.id.dialog_text_view_direccion ) ;
        dialog_comentario= view.findViewById( R.id.dialog_comentario ) ;

        //Autocompletado cuando creas un platillo
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>
                (getContext(), android.R.layout.select_dialog_item, Constants.foods);
        acPlatillo = (AutoCompleteTextView) view.findViewById(R.id.acPlatillos);
        acPlatillo.setThreshold(1);//will start working from first character
        acPlatillo.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        acPlatillo.setTextColor(Color.RED);


        dialog_iv_foto= view.findViewById( R.id.dialog_imageView );
        dialogButtonsi= view.findViewById( R.id.dialog_yes );
        ratingBar_dialog=view.findViewById( R.id.dialog_rating_bar );
        //on click
        dialog_iv_foto.setOnClickListener( this );
        dialogButtonsi.setOnClickListener( this );
        if(address==null){
            getDeviceLocation();
        }else{
            String adress=address.get( 0 ).getAddressLine( 0 ) ;
            String locality=address.get( 0 ).getSubLocality(  ) ;
            dialog_et_direccion.setText( adress+" barrio "+ locality );
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,@Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        switch (requestCode){
            case  TAKEFOTO:
                if (resultCode==RESULT_OK){
                    bitmap=(Bitmap) data.getExtras().get( "data" );
                    dialog_iv_foto.setImageBitmap( bitmap );
                    fileImage = data.getData();
                }else {
                    Toast.makeText( getContext(),"Error de data no se obtuvo" ,Toast.LENGTH_SHORT).show();
                }
                break;
            case GPS:
                if (resultCode==RESULT_OK){
                    getDeviceLocation();

                }else {
                    Toast.makeText( getContext(),"Error de gps" ,Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @SuppressLint("MissingPermission")
    private void getDeviceLocation() {
        fusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener( new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()){
                            location= task.getResult();
                            if(location!=null){
                                mMap.moveCamera( CameraUpdateFactory.newLatLngZoom( new LatLng( location.getLatitude(),location.getLongitude() ),18) );

                              try {

                                    geocoder= new Geocoder( context, Locale.getDefault() );
                                    address= geocoder.getFromLocation( location.getLatitude(),location.getLongitude(),1 );
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }else {
                                LocationRequest locationRequest= LocationRequest.create();
                                locationRequest.setInterval( 10000 );
                                locationRequest.setFastestInterval( 5000 );
                                locationRequest.setPriority( LocationRequest.PRIORITY_HIGH_ACCURACY );

                                locationCallback= new LocationCallback(){
                                    @Override
                                    public void onLocationResult(LocationResult locationResult) {
                                        super.onLocationResult( locationResult );
                                        if (locationResult==null)
                                            return;
                                        location = locationResult.getLastLocation();

                                        mMap.moveCamera( CameraUpdateFactory.newLatLngZoom( new LatLng( location.getLatitude(),location.getLongitude() ),18) );

                                        try {

                                            geocoder= new Geocoder( context, Locale.getDefault() );
                                            address= geocoder.getFromLocation( location.getLatitude(),location.getLongitude(),1 );
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        fusedLocationProviderClient.removeLocationUpdates( locationCallback );
                                    }
                                };

                                fusedLocationProviderClient.requestLocationUpdates( locationRequest,locationCallback,null );

                            }

                        }else{
                            Toast.makeText( getContext(),"Unable to get last lcoation" ,Toast.LENGTH_SHORT).show();
                        }
                    }
                } );
    }


    @Override
    public boolean onMarkerClick(Marker marker) {

        for(int i = 0; i <  arrayListPlatillos.size(); i++)
            if(arrayListPlatillos.get(i).getNombrePlatillo().equals(markerMapPlatillos.get(marker.getId())))
                rvListaPlatillos.smoothScrollToPosition(i);

        return false;
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        context=getContext();
        super.onAttachFragment(childFragment);
    }

}