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
import com.google.android.gms.tasks.Continuation;
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
import com.google.firebase.storage.UploadTask;
import com.singlefood.sinfo.R;
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
public class mapsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMyLocationClickListener, GoogleMap.OnMyLocationButtonClickListener, ActivityCompat.OnRequestPermissionsResultCallback, View.OnClickListener {
    View view;
    MapView mapView;
    Dialog dialog;
    Uri fileImage;
    Bitmap bitmap;
    Marker m;
    private int TAKEFOTO=1;
    private RecyclerView.Adapter adapterRview;
    private RecyclerView.LayoutManager layourRview;
    private RecyclerProductoAdapter mAdapter;
    private RecyclerView Rview;
    private GoogleMap mMap;
    ProgressDialog progressDialog;
    FloatingActionButton fab;
    List<Address> address;
    private List<String> nombres;
    Geocoder geocoder;
    private StorageReference mStorageReference;
    private DatabaseReference mDatabase; //FIREBASE
    FusedLocationProviderClient fusedLocationProviderClient; //Ultima Ubicacion
    LocationRequest locationRequest; //Actualizar posicion
    LocationCallback locationCallback; //ACtualizar posicion
    Location location;
    //Dialog datos
    EditText dialog_et_nombre;
    EditText dialog_et_precio;
    Spinner dialog_spinner_tipo;
    ImageView dialog_iv_foto;

    private ArrayList<Marker> tmpRealTimeMarkers = new ArrayList<>(); //Array Marcadores temporales de almacenamiento para hacer llamado
    private ArrayList<Marker> realTimeMarkers = new ArrayList<>();     //Marcadores tiempo real

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
//            fab.show();
//            Toast.makeText( getContext(),"Presiona Boton GPS y Activa permiso",Toast.LENGTH_LONG ).show();
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
//        }else {
//            fab.hide();
//        }
        return view;
    }
    private void borrar(int position){
        this.nombres.remove(position);
        this.adapterRview.notifyItemRemoved(position);

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
//        adapterRview = new MyAdapter(nombres, R.layout.list_single_card, new MyAdapter.OnItemClickListener() {
//            @Override
//            public void OnClickListener(String name, int position) {
//                borrar(position);
//
//            }
//        });
//        Rview.setLayoutManager(layourRview);
//        Rview.setAdapter(adapterRview);
        mStorageReference= FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference(); //Instanciar BD Firebase
        initFused();
    }


    //    private Boolean isGpsenabled(){
//        try {
//            int gps= Settings.Secure.getInt( getActivity().getContentResolver(),Settings.Secure.LOCATION_MODE );
//            Toast.makeText( getContext(),"bootn",Toast.LENGTH_SHORT ).show();
//            if (gps==0){
//               return  false;
//            }else{
//                return  true;
//            }
//        } catch (Settings.SettingNotFoundException e) {
//            e.printStackTrace();
//            Toast.makeText( getContext(),"error",Toast.LENGTH_SHORT ).show();
//            return  false;
//        }
//    }
//    private void showAlertgps(){
//        new AlertDialog.Builder(getContext())
//                .setTitle( "Señal GPS" )
//                .setMessage( "¿Deseas Activar el gps para precision?" )
//                .setPositiveButton( "Si", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Intent i= new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS );
//                        startActivity( i );
//                    }
//                } )
//                .setNegativeButton( "Cancel",null )
//                .show();
//    }
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
//        mMap.isMyLocationEnabled();
        if (ActivityCompat.checkSelfPermission( getContext(), Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( getContext(), Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled( true );
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        mMap.getUiSettings().setCompassEnabled( false );
        mMap.getUiSettings().setIndoorLevelPickerEnabled( false );
        mDatabase.child("Platillos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(Marker marker:realTimeMarkers){
                    marker.remove();
                }
                final ArrayList<Platillos> arrayListPlatillos= new ArrayList<>(  );
                final ArrayList<String> arrayKeys= new ArrayList<>(  );
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){


///no modificar************************************************************
                    Platillos platillos= snapshot.getValue(Platillos.class);
                    Double latitud = platillos.getPlaces().getLatitud();
                    Double longitud = platillos.getPlaces().getLongitud();
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(new LatLng(latitud,longitud));
                    markerOptions.icon( BitmapDescriptorFactory.fromResource(R.mipmap.tacho_general));
                    arrayListPlatillos.add( platillos );
                    arrayKeys.add( snapshot.getKey() );
// hasta aca*****************************//nuevo adapter****************************************************
                    adapterRview = new RecyclerProductoAdapter(getContext(), R.layout.list_single_card, arrayListPlatillos, new RecyclerProductoAdapter.OnItemClickListener() {
                                            @Override
                                            public void OnClickListener(Platillos platillos, int position) {
                                                Toast.makeText( getContext(),"Seleccionado:  "+position+"+"+arrayKeys.get( position ), Toast.LENGTH_SHORT ).show();
                                                Fragment fragment = new informacionFragment();
                                                Bundle args = new Bundle();
                                                args.putSerializable("datos", arrayListPlatillos);
                                                args.putInt( "key",position );
                                                fragment.setArguments(args);
                                                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                                transaction.replace(R.id.content_frame, fragment);
                                                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                                                transaction.addToBackStack(null);
                                                transaction.commit();

                                            }
                                        } );

                    Rview.setAdapter(adapterRview);

//                    try {
//
//                        geocoder= new Geocoder( getContext(), Locale.getDefault() );
//                        address= geocoder.getFromLocation( latitud,longitud,1 );
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }

                    markerOptions.title("Be Clean, with RotClean");
                    tmpRealTimeMarkers.add(mMap.addMarker(markerOptions));
                }
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
    public void onMyLocationClick(@NonNull Location location) {
        if(m==null){
            Toast.makeText(getContext(), "Primero", Toast.LENGTH_SHORT).show();
            LatLng dato = new LatLng( location.getLatitude(),location.getLongitude() );
            MarkerOptions a = new MarkerOptions().position(dato).title( "Actual" ).draggable( true ) ;
            m = mMap.addMarker(a);
            mMap.moveCamera( CameraUpdateFactory.newLatLng( dato ) );
            CameraUpdate zoom= CameraUpdateFactory.zoomTo( 15 );
            mMap.animateCamera( zoom );
        }else {
            Toast.makeText(getContext(), "Primero", Toast.LENGTH_SHORT).show();
            LatLng dato2 = new LatLng( location.getLatitude(), location.getLongitude() );
            m.setPosition( dato2 );
            mMap.moveCamera( CameraUpdateFactory.newLatLng( dato2) );
            CameraUpdate zoom= CameraUpdateFactory.zoomTo( 20 );
            mMap.animateCamera( zoom );
        }

    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(getActivity(), "Mi ubicacion", Toast.LENGTH_SHORT).show();
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
                progressDialog.setMessage( "Subiendo a la Nube" );
                progressDialog.setCancelable( false );
                progressDialog.show();
                String platillo=dialog_et_nombre.getText().toString().trim();
                String precio=dialog_et_precio.getText().toString().trim();

                if (platillo.equals( "" ) && precio.equals( "" )){
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
    private void guardarDatosFirebaseDialog(String platillo, String precio)  {
        final Map<String,Object> datos=new HashMap<>(  );
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
            if(bitmap!=null) {
                final StorageReference fotoref = mStorageReference.child( "Fotos" ).child( fileImage.getLastPathSegment() );
                fotoref.putFile( fileImage ).continueWithTask( new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw new Exception();
                        }
                        return fotoref.getDownloadUrl();
                    }
                } ).addOnCompleteListener( new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadLink = task.getResult();
                                datos.put( "nombrePlatillo", dialog_et_nombre.getText().toString().trim() );
                                datos.put( "precio", dialog_et_precio.getText().toString().trim() );
                                datos.put( "tipo", dialog_spinner_tipo.getSelectedItem().toString() );
                                datos.put( "imagen", downloadLink.toString() );
                                datos.put( "imagenbitmap", bitmap );
                                mDatabase.child( "Platillos" ).push().setValue( datos ).addOnCompleteListener( new OnCompleteListener<Void>() {
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


                        }
                    }
                } );
            }else {
                progressDialog.dismiss();
                Toast.makeText( getContext(),"Error File vacio",Toast.LENGTH_SHORT ).show();
            }
    }
    private void guardarDatosFirebaseDialogNotDatabase(String platillo, String precio)  {
        final Map<String,Object> datos=new HashMap<>(  );
        final Map<String,Object> base_datos=new HashMap<>(  );
        base_datos.put( "ciudad", address.get( 0 ).getAddressLine( 0 ) );
        base_datos.put( "direccion", address.get( 0 ).getLocality() );
        base_datos.put( "latitud", address.get( 0 ).getLatitude() );
        base_datos.put( "longitud", address.get( 0 ).getLongitude() );
        base_datos.put( "id_user", "prueba001" );

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
            datos.put( "imagenbitmap", bitmap );
            datos.put( "imagenbase64", imageString );
            datos.put( "places",base_datos);

            mDatabase.child( "Platillos" ).push().setValue( datos ).addOnCompleteListener( new OnCompleteListener<Void>() {
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
        dialog_et_nombre=(EditText) dialog.findViewById( R.id.dialog_edit_text_nombre ) ;
        dialog_et_precio=(EditText) dialog.findViewById( R.id.dialog_edit_text_precio ) ;
        dialog_spinner_tipo=(Spinner) dialog.findViewById( R.id.dialog_spinner ) ;
        dialog_iv_foto=(ImageView) dialog.findViewById( R.id.dialog_imageView );
        Button dialogButtonsi=(Button) dialog.findViewById( R.id.dialog_yes );
        Button dialogButtonno=(Button) dialog.findViewById( R.id.dialog_no );
        TextView dialogTextDireccion=dialog.findViewById( R.id.dialog_text_view_direccion );
        TextView dialogTextCiudad=dialog.findViewById( R.id.dialog_text_view_ciudad );
        TextView dialogTextLatitud=dialog.findViewById( R.id.dialog_text_view_latitud );
        TextView dialogTextLongitud=dialog.findViewById( R.id.dialog_text_view_longitud );
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
        dialogTextLatitud.setText( Double.toString( latitud ) );
        dialogTextLongitud.setText( Double.toString( longitud )  );

// Create an ArrayAdapter using the string array and a default spinner layout
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
//                R.array.planets_array, android.R.layout.simple_spinner_item);
//// Specify the layout to use when the list of choices appears
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//// Apply the adapter to the spinner
//        spinner.setAdapter(adapter);
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
        //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

    }

}


//public class mapsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMyLocationClickListener, GoogleMap.OnMyLocationButtonClickListener, ActivityCompat.OnRequestPermissionsResultCallback, View.OnClickListener, GoogleMap.OnMarkerDragListener {
//    View view;
//    MapView mapView;
//    private GoogleMap mMap;
//    FloatingActionButton fab;
//    private List<String> nombres;
//    private int counter=0;
//    List<Address> adres;
//    private RecyclerView Rview;
//    private Dialog dialog;
//    Geocoder geo;
//    Marker m;
//    private RecyclerView.Adapter adapterRview;
//    private RecyclerView.LayoutManager layourRview;
//
//    private DatabaseReference mDatabase; //FIREBASE
//    FusedLocationProviderClient fusedLocationProviderClient; //Ultima Ubicacion
//    LocationRequest locationRequest; //Actualizar posicion
//    LocationCallback locationCallback; //ACtualizar posicion
//    Location location;
//
//    private ArrayList<Marker> tmpRealTimeMarkers = new ArrayList<>(); //Array Marcadores temporales de almacenamiento para hacer llamado
//    private ArrayList<Marker> realTimeMarkers = new ArrayList<>();     //Marcadores tiempo real
//
//    public mapsFragment() {
//        // Required empty public constructor
//    }
//
//    @TargetApi(Build.VERSION_CODES.M)
//    @RequiresApi(api = Build.VERSION_CODES.M)
//    @SuppressLint("WrongConstant")
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        view = inflater.inflate( R.layout.fragment_maps, container, false );
//         fab = (FloatingActionButton) view.findViewById( R.id.floatingbar_save );
//         fab.setOnClickListener(this );
//        layourRview = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
//        adapterRview = new MyAdapter(nombres, R.layout.list_single_card, new MyAdapter.OnItemClickListener() {
//            @Override
//            public void OnClickListener(String name, int position) {
//                borrar(position);
//
//            }
//        });
//        Rview.setLayoutManager(layourRview);
//        Rview.setAdapter(adapterRview);
//        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
////            fab.show();
////            Toast.makeText( getContext(),"Presiona Boton GPS y Activa permiso",Toast.LENGTH_LONG ).show();
//            new AlertDialog.Builder( getContext() )
//                    .setTitle( "Activa Permiso" )
//                    .setMessage( "Permiso desabilitado!! " )
//                    .setPositiveButton( "Si", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            final Intent i = new Intent();
//                            i.setAction( Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                            i.addCategory(Intent.CATEGORY_DEFAULT);
//                            i.setData( Uri.parse("package:" + getContext().getPackageName()));
//                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                            i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//                            getContext().startActivity(i);
//                        }
//                    } )
//                    .setNegativeButton( "No", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            FragmentTransaction ft = getFragmentManager().beginTransaction();
//                            if (Build.VERSION.SDK_INT >= 26) {
//                                ft.setReorderingAllowed(false);
//                            }
//                            ft.detach(mapsFragment.this).attach(mapsFragment.this).commit();
//                        }
//                    } )
//                    .show();
//        }
////        }else {
////            fab.hide();
////        }
//        return view;
//    }
//    private List<String> getAllnames(){
//        return new ArrayList<String>(){{
//            add("Elias");
//            add("Antonio");
//            add("Cesar");
//            add("Willy");
//        }};
//    }
//    private void borrar(int position){
//        this.nombres.remove(position);
//        this.adapterRview.notifyItemRemoved(position);
//
//    }
//    @Override
//    public void onMarkerDragStart(Marker marker) {
//
//    }
//
//    @Override
//    public void onMarkerDrag(Marker marker) {
//
//    }
//
//    @Override
//    public void onMarkerDragEnd(Marker marker) {
//        double latitud = marker.getPosition().latitude;
//        double longitud = marker.getPosition().longitude;
//        try {
//            adres=geo.getFromLocation( latitud,longitud,1 );
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        String adress=adres.get( 0 ).getAddressLine( 0 ) ;
//        String city=adres.get( 0 ).getLocality(  ) ;
//        String state=adres.get( 0 ).getAdminArea( ) ;
//        String country=adres.get( 0 ).getCountryName(  ) ;
//        String postal=adres.get( 0 ).getPostalCode(  ) ;
//        double latitud2=adres.get( 0 ).getLatitude() ;
//        double longitud3=adres.get( 0 ).getLongitude(  ) ;
//
//        Toast.makeText( getContext(),"Latitud: "+ latitud2+ "\n" +
//                "Longitud: "+longitud3+"\n"+
//                "City: "+city+"\n"+
//                "State: "+state+"\n"+
//                "Country: "+country+"\n"+
//                "PostalCode: "+postal,Toast.LENGTH_SHORT ).show();
//
//    }
//    public  void cargarProducto(String nombre,Double precio){
//        dialog =new Dialog( getContext() );
//        dialog.setContentView( R.layout.dialog_eow );
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable( Color.TRANSPARENT ) );
//
//        Button dialogButtonsi=(Button) dialog.findViewById( R.id.dialog_yes );
//        Button dialogButtonno=(Button) dialog.findViewById( R.id.dialog_no );
//        dialogButtonsi.setOnClickListener( this );
//        dialogButtonno.setOnClickListener( this );
//        Spinner spinner = (Spinner) dialog.findViewById(R.id.dialog_spinnerTipo);
//// Create an ArrayAdapter using the string array and a default spinner layout
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
//                R.array.planets_array, android.R.layout.simple_spinner_item);
//// Specify the layout to use when the list of choices appears
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//// Apply the adapter to the spinner
//        spinner.setAdapter(adapter);
//        dialog.show();
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated( view, savedInstanceState );
//
//        mapView = (MapView) view.findViewById( R.id.map );
//        if (mapView != null) {
//            mapView.onCreate( null );
//            mapView.onResume();
//            mapView.getMapAsync( this );
//        }
//
//
//        mDatabase = FirebaseDatabase.getInstance().getReference(); //Instanciar BD Firebase
//        initFused();
//    }
//    //    private Boolean isGpsenabled(){
////        try {
////            int gps= Settings.Secure.getInt( getActivity().getContentResolver(),Settings.Secure.LOCATION_MODE );
////            Toast.makeText( getContext(),"bootn",Toast.LENGTH_SHORT ).show();
////            if (gps==0){
////               return  false;
////            }else{
////                return  true;
////            }
////        } catch (Settings.SettingNotFoundException e) {
////            e.printStackTrace();
////            Toast.makeText( getContext(),"error",Toast.LENGTH_SHORT ).show();
////            return  false;
////        }
////    }
////    private void showAlertgps(){
////        new AlertDialog.Builder(getContext())
////                .setTitle( "Señal GPS" )
////                .setMessage( "¿Deseas Activar el gps para precision?" )
////                .setPositiveButton( "Si", new DialogInterface.OnClickListener() {
////                    @Override
////                    public void onClick(DialogInterface dialog, int which) {
////                        Intent i= new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS );
////                        startActivity( i );
////                    }
////                } )
////                .setNegativeButton( "Cancel",null )
////                .show();
////    }
//    @Override
//    public void onResume() {
//        super.onResume();
//    }
//    private void guardarLatitudLongitud() {
//
//        String adress=adres.get( 0 ).getAddressLine( 0 ) ;
//        String city=adres.get( 0 ).getLocality(  ) ;
//        double latitud=adres.get( 0 ).getLatitude() ;
//        double longitud=adres.get( 0 ).getLongitude(  ) ;
//        Map<String,Object> latlang=new HashMap<>(  );
//        latlang.put( "latitud",latitud );
//        latlang.put( "longitud",longitud );
//        latlang.put( "direccion",adress );
//        latlang.put( "ciudad",city );
//
//
//    }
//
//    private void initFused() {
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient( getContext() );
//        if (ActivityCompat.checkSelfPermission( getContext(), Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( getContext(), Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        buildLocationRequest();
//        buildLocationCallBack();
//        fusedLocationProviderClient.requestLocationUpdates( locationRequest, locationCallback, Looper.myLooper() );
//    }
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
////        mMap.isMyLocationEnabled();
//        if (ActivityCompat.checkSelfPermission( getContext(), Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( getContext(), Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        mMap.setMyLocationEnabled( true );
//        mMap.setOnMyLocationButtonClickListener(this);
//        mMap.setOnMyLocationClickListener(this);
//        mDatabase.child("tachos").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                for(Marker marker:realTimeMarkers){
//                    marker.remove();
//                }
//
//                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
//
//                    Tachos tachos= snapshot.getValue( Tachos.class);
//                    Double latitud = tachos.getLatitud();
//                    Double longitud = tachos.getLongitud();
//                    MarkerOptions markerOptions = new MarkerOptions();
//                    markerOptions.position(new LatLng(latitud,longitud));
//                    markerOptions.icon( BitmapDescriptorFactory.fromResource(R.drawable.ic_place_black_24dp));
//
//                    try {
//
//                        geo= new Geocoder( getContext(), Locale.getDefault() );
//                        adres= geo.getFromLocation( latitud,longitud,1 );
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                    String adress=adres.get( 0 ).getAddressLine( 0 ) ;
//                    String city=adres.get( 0 ).getLocality(  ) ;
//                    String state=adres.get( 0 ).getAdminArea( ) ;
//                    String country=adres.get( 0 ).getCountryName(  ) ;
//                    String postal=adres.get( 0 ).getPostalCode(  ) ;
//                    markerOptions.title("Be Clean, with RotClean"+"\n" +
//                            "City: "+city+"\n"+
//                            "State: "+state+"\n"+
//                            "Country: "+country+"\n"+
//                            "PostalCode: "+postal);
//                    //markerOptions.title("Be Clean, with RotClean");
//                    tmpRealTimeMarkers.add(mMap.addMarker(markerOptions));
//                }
//                realTimeMarkers.clear();
//                realTimeMarkers.addAll(tmpRealTimeMarkers);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//        buildLocationRequest();
//        buildLocationCallBack();
//        mMap.getUiSettings().setCompassEnabled( false );
//        mMap.getUiSettings().setIndoorLevelPickerEnabled( false );
//
//        geo= new Geocoder( getContext(), Locale.getDefault() );
//        LatLng dato = new LatLng( -16.387378,-71.5421 );
//        MarkerOptions a = new MarkerOptions().position(dato).title( "Actual" ).draggable( true ) ;
//        m = mMap.addMarker(a);
//        mMap.moveCamera( CameraUpdateFactory.newLatLng( dato ) );
//        CameraUpdate zoom= CameraUpdateFactory.zoomTo( 15 );
//        mMap.animateCamera( zoom );
//    }
//    private void buildLocationCallBack(){
//
//        locationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                super.onLocationResult(locationResult);
//                location = locationResult.getLocations().get(locationResult.getLocations().size() - 1);
//                Log.e("Location", "" + location.getLatitude() + "" + location.getLongitude());
//
//                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    return;
//                }
//                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
//                    @Override
//                    public void onSuccess(Location location) {
//                        //OBTENER ULTIMA UBICACION DEL DISPOSITIVO
//                        if (location != null) {
//
//
//                        }
//                        LatLng arequipa= new LatLng( 	-16.387378,-71.5421 );
//                        CameraPosition camera=new CameraPosition.Builder()
//                                .target( arequipa )
//                                .zoom( 15 )
//                                .build();
//                        mMap.animateCamera( CameraUpdateFactory.newCameraPosition( camera ) );
//                        // mMap.animateCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()),15.0f));
//                    }
//                });
//            }
//
//        };
//    }
//    private void buildLocationRequest(){
//        locationRequest = new LocationRequest();
//        locationRequest.setPriority( LocationRequest.PRIORITY_HIGH_ACCURACY);
//        locationRequest.setInterval(5000);
//        locationRequest.setFastestInterval(3000);
//        locationRequest.setSmallestDisplacement(10);
//    }
//
//    @Override
//    public void onMyLocationClick(@NonNull Location location) {
//
//    }
//
//    @Override
//    public boolean onMyLocationButtonClick() {
//        Toast.makeText(getActivity(), "Mi ubicacion", Toast.LENGTH_SHORT).show();
//        return false;
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (view.getId()){
//            case R.id.dialog_no:
//                dialog.dismiss();
//                break;
//            case R.id.dialog_yes:
//                dialog.dismiss();
//                break;
//            case R.id.floatingbar_save:
//                if(adres==null){
//                    Toast.makeText( getContext(), "Mueva el cursos por favor", Toast.LENGTH_SHORT ).show();
//                }
//                else {
////                    String adress=adres.get( 0 ).getAddressLine( 0 ) ;
////                    String city=adres.get( 0 ).getLocality(  ) ;
////                    new AlertDialog.Builder(MapsActivity.this)
////                            .setTitle( "Desea guardar en Guardar")
////                            .setMessage( ""+ "direccion: "+adress+"\n"+ "ciudad: "+city+"\n"  )
////                            .setPositiveButton( "Si", new DialogInterface.OnClickListener() {
////                                @Override
////                                public void onClick(DialogInterface dialog, int which) {
////                                    guardarLatitudLongitud();
////                                }
////                            } )
////                            .setNegativeButton( "No",null )
////                            .show();
//                    cargarProducto( "Hola",15.2 );
//                }
//                break;
//        }
////        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
////            Toast.makeText( getContext(),"Permiso desabilitado" ,Toast.LENGTH_SHORT).show();
////            final Intent i = new Intent();
////            i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
////            i.addCategory(Intent.CATEGORY_DEFAULT);
////            i.setData(Uri.parse("package:" + getContext().getPackageName()));
////            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////            i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
////            i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
////            getContext().startActivity(i);
////
////        }else {
////            Toast.makeText( getContext(),"Funcionamiento gps con normalidad" ,Toast.LENGTH_SHORT).show();
////
////            FragmentTransaction ft = getFragmentManager().beginTransaction();
////            if (Build.VERSION.SDK_INT >= 26) {
////                ft.setReorderingAllowed(false);
////            }
////            ft.detach(this).attach(this).commit();
////        }
//    }
//}

