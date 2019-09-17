package com.singlefood.sinfo.models;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
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
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.ApiException;
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
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;
import com.singlefood.sinfo.LoginActivity;
import com.singlefood.sinfo.R;
import com.singlefood.sinfo.models.productos.Comentarios;
import com.singlefood.sinfo.models.productos.Platillos;
import com.singlefood.sinfo.models.productos.RecyclerProductoAdapter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.INPUT_METHOD_SERVICE;

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
    private final int TAKEFOTO=1;
    private final int GPS=51;
    private static final int REQUEST_CAPTURE_IMAGE = 100;
    String imageFilePath;
    FirebaseStorage store;
    StorageReference storageReference;
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
    private List<AutocompletePrediction> predictionList;
    //Dialog datos
    private AutoCompleteTextView acPlatillo;
    private EditText dialog_et_precio;
    private  Spinner dialog_spinner_tipo;
    private ImageView dialog_iv_foto;
    private RatingBar ratingBar_dialog;
    private MaterialSearchBar searchView;
    private ArrayList<Marker> tmpRealTimeMarkers = new ArrayList<>(); //Array Marcadores temporales de almacenamiento para hacer llamado
    private ArrayList<Marker> realTimeMarkers = new ArrayList<>();     //Marcadores tiempo real

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;


    final ArrayList<String> llaves= new ArrayList<>();

    //Declare HashMap to store mapping of marker to Activity
    final HashMap<String, String> markerMapPlatillos = new HashMap<String, String>();

    public mapsFragment() {
        // Required empty public constructor
    }

    String[] foods = {"Adobo de chancho",
            "Aguadito",
            "Ajiaco de Papas",
            "Ají de Gallina",
            "Alverjado de Pollo",
            "Anticuchos",
            "Arroz a la jardinera",
            "Arroz Chaufa",
            "Arroz chaufa de mariscos",
            "Arroz con mariscos",
            "Arroz con pato",
            "Arroz con pollo",
            "Arroz tapado",
            "Bistec a lo pobre",
            "Bistec a la chorrilana",
            "Cabrito a la norteña",
            "Caldo de cabeza de carnero o cordero",
            "Caldo de gallina",
            "Cancancho o cordero al palo",
            "Cau cau",
            "Causa",
            "Causa lambayecana o ferreñafana",
            "Ceviche",
            "Ceviche de conchas negras",
            "Ceviche de camarones",
            "Ceviche mixto",
            "Carapulcra",
            "Cuy chactado",
            "Chacharada o cacharrada",
            "Chairo",
            "Chanfainita",
            "Charquicán",
            "Chaque",
            "Chicharrones",
            "Chilcano",
            "Chinguirito",
            "Chirimpico",
            "Choros a la chalaca",
            "Chupe de cangrejos",
            "Chupe de camarones",
            "Chupe verde o Yacuchupe",
            "Escabeche",
            "Escribano",
            "Estofado",
            "Espesado",
            "Frito trujillano",
            "Hígado encebollado",
            "Huatía",
            "Inchicapi",
            "Jalea",
            "Juane",
            "Locro",
            "Locro de gallina",
            "Lomo saltado",
            "Malaya",
            "Majarisco",
            "Menestrón",
            "Migadito",
            "Mondonguito a la italiana",
            "Ocopa",
            "Olluquito con charqui",
            "Pachamanca",
            "Papa a la Huancaína",
            "Papa rellena",
            "Parihuela",
            "Patachi o sopa de trigo",
            "Patarashca",
            "Patasca",
            "Patitas con maní",
            "Pepian de choclo",
            "Pesque de quinua",
            "Pescado a lo macho",
            "Picante a la tacneña",
            "Picante de cuy",
            "Pollo broaster",
            "Pollo a la brasa",
            "Pollo al sillao",
            "Puca picante",
            "Quinua atamalada",
            "Rocoto relleno",
            "Salchipapa",
            "Sancochado",
            "Seco de chabelo",
            "Seco de res con frejoles",
            "Seco a la norteña",
            "Shambar",
            "Solterito",
            "Sopa criolla",
            "Sopa de choros",
            "Sopa seca",
            "Sopa teóloga",
            "Sudado de pescado",
            "Tacacho con cecina",
            "Tacu tacu",
            "Tallarín saltado",
            "Tallarines rojos con pollo",
            "Tallarines verdes",
            "Tiradito",
            "Tortilla de raya",
            "Trucha frita"};

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

//        store= FirebaseStorage.getInstance();
//        storageReference= store.getReference();
//        searchView= (SearchView) view.findViewById( R.id.maps_search_view );
//        searchView.setOnQueryTextListener( new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                String location = searchView.getQuery().toString();
//                List<Address> addressList=null;
//                if(location!=null || !location.equals( "" )){
//                    Geocoder geocoder = new Geocoder( getContext() );
//                    try {
//                        addressList=geocoder.getFromLocationName( location,1 );
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    Address address= addressList.get( 0 );
//                    LatLng latLng= new LatLng( address.getLatitude(),address.getLongitude() );
//                    mMap.animateCamera( CameraUpdateFactory.newLatLngZoom( latLng,15 ) );
//
//                }
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return false;
//            }
//        } );
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

        Rview = view.findViewById(R.id.my_recycler_view);
        layourRview = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        Rview.setLayoutManager(layourRview);

        mStorageReference= FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference(); //Instanciar BD Firebase
        //initFused();
        CargarSearch();
    }

    private void CargarSearch() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient( getContext() );
        Places.initialize( getContext(),"AIzaSyAgSv7wL2PTgdXSiKggKstMiiPYT-87zb4" );
        PlacesClient placesClient= Places.createClient( getContext() );
        AutocompleteSessionToken token= AutocompleteSessionToken.newInstance();
        searchView= (MaterialSearchBar) view.findViewById( R.id.searchBar );
        searchView.setOnSearchActionListener( new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                getActivity().startSearch( text.toString(),true,null,true );

            }

            @Override
            public void onButtonClicked(int buttonCode) {
                switch (buttonCode){
                    case MaterialSearchBar.BUTTON_NAVIGATION:

                    case MaterialSearchBar.BUTTON_BACK:
                        searchView.disableSearch();
                        break;
                }
            }
        } );
        searchView.addTextChangeListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                FindAutocompletePredictionsRequest predictionsRequest= FindAutocompletePredictionsRequest.builder()
                        .setCountry( "PE" )
                        .setTypeFilter( TypeFilter.ADDRESS )
                        .setSessionToken( token )
                        .setQuery( charSequence.toString() )
                        .build();
                placesClient.findAutocompletePredictions( predictionsRequest ).addOnCompleteListener( new OnCompleteListener<FindAutocompletePredictionsResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<FindAutocompletePredictionsResponse> task) {
                        if (task.isSuccessful()){
                            FindAutocompletePredictionsResponse predictionsResponse=task.getResult();
                            if(predictionsResponse!=null){
                                predictionList=predictionsResponse.getAutocompletePredictions();
                                List<String> suggestionPrediccion= new ArrayList<>(  );
                                for (int i=0;i<predictionList.size();i++){
                                    AutocompletePrediction prediction= predictionList.get( i );
                                    suggestionPrediccion.add( prediction.getFullText( null ).toString() );
                                }
                                searchView.updateLastSuggestions( suggestionPrediccion );
                                if(!searchView.isSuggestionsVisible()){
                                    searchView.showSuggestionsList();
                                }
                            }
                        }else {
                            Log.i( "mytag: ","prediccion is fetichng succesful" );
                        }
                    }
                } );

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        } );
        searchView.setSuggestionsClickListener( new SuggestionsAdapter.OnItemViewClickListener() {
            @Override
            public void OnItemClickListener(int position, View v) {
                if (position>=predictionList.size()){
                    return;
                }
                AutocompletePrediction selectedPrediction= predictionList.get( position );
                String suggestion= searchView.getLastSuggestions().get( position ).toString();
                searchView.setText( suggestion );
                searchView.clearSuggestions();
                InputMethodManager inputMethodManager=(InputMethodManager) getContext().getSystemService( INPUT_METHOD_SERVICE );
                if (inputMethodManager!=null){
                    inputMethodManager.hideSoftInputFromWindow( searchView.getWindowToken(),InputMethodManager.HIDE_IMPLICIT_ONLY );

                }
                String placeID=selectedPrediction.getPlaceId();
                List<Place.Field> placeFields= Arrays.asList(Place.Field.LAT_LNG);
                FetchPlaceRequest fetchPlaceRequest= FetchPlaceRequest.builder( placeID,placeFields ).build();
                placesClient.fetchPlace( fetchPlaceRequest ).addOnSuccessListener( new OnSuccessListener<FetchPlaceResponse>() {
                    @Override
                    public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                        Place place= fetchPlaceResponse.getPlace();
                        Log.i( "mytag: ","Place  found" + place.getName());
                        LatLng latLngplace=place.getLatLng();
                        if(latLngplace!=null){
                            mMap.moveCamera( CameraUpdateFactory.newLatLngZoom( latLngplace,18 ) );

                        }
                    }
                } ).addOnFailureListener( new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(e instanceof ApiException){
                            ApiException apiException= (ApiException) e;
                            apiException.printStackTrace();
                            int statusCode=apiException.getStatusCode();
                            Log.i( "mytag: ","Place no found" + e.getMessage() );
                            Log.i( "mytag: ","Status Code" + statusCode );

                        }
                    }
                } );
            }

            @Override
            public void OnItemDeleteListener(int position, View v) {

            }
        } );
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initFused() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient( getContext() );
        Places.initialize( getContext(),"AIzaSyAgSv7wL2PTgdXSiKggKstMiiPYT-87zb4" );
        PlacesClient placesClient= Places.createClient( getContext() );
        AutocompleteSessionToken token= AutocompleteSessionToken.newInstance();

        if (ActivityCompat.checkSelfPermission( getContext(), Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( getContext(), Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
       // buildLocationRequest();
       // buildLocationCallBack();
        fusedLocationProviderClient.requestLocationUpdates( locationRequest, locationCallback, Looper.myLooper() );
    }
    private void UploadImage(){
        FirebaseAuth mauth=FirebaseAuth.getInstance();
        FirebaseUser user = mauth.getCurrentUser();
        StorageReference ref = storageReference.child( user.getUid()+"/"+ UUID.randomUUID().toString() );
        ref.putFile( fileImage )
                .addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText( getContext(),"Subido imagen", Toast.LENGTH_SHORT ).show();
                    }
                } )
                .addOnFailureListener( new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText( getContext(),"Fallo subir imagen", Toast.LENGTH_SHORT ).show();
                    }
                } )
                .addOnProgressListener( new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText( getContext(),"contador imagen", Toast.LENGTH_SHORT ).show();
                    }
                } );
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
            layoutParams.setMargins( 0,0,80,300  );
        }

        mDatabase.child("Platillos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(Marker marker:realTimeMarkers){
                    marker.remove();
                }
                 ArrayList<Platillos> arrayListPlatillos= new ArrayList<>();
                 ArrayList<ArrayList<Comentarios>> arrayKeys= new ArrayList<>();
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
//        buildLocationRequest();
//        buildLocationCallBack();
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
        if (searchView.isSuggestionsVisible()){
            searchView.clearSuggestions();
        }
        if (searchView.isSearchEnabled()){
            searchView.disableSearch();
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.floatingbar_save:
                Toast.makeText( getContext(),"Buton",Toast.LENGTH_SHORT ).show();
                   // cargarProducto(  );
                     FirebaseAuth mAuth= FirebaseAuth.getInstance();
                FirebaseUser user=mAuth.getCurrentUser();
                if(user != null){
                    cargarProducto(  );
                }else{
                    Intent intent = new Intent( getContext(), LoginActivity.class);
                    startActivity(intent);
                }


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
            case  R.id.dialog_no:
                dialog.dismiss();
                Toast.makeText( getContext(),"Datos No Guardados", Toast.LENGTH_SHORT ).show();
                break;
            case R.id.dialog_imageView:
                Intent intent= new Intent( );
                intent.setAction(  MediaStore.ACTION_IMAGE_CAPTURE );
                startActivityForResult( intent,TAKEFOTO );
                //openCameraIntent();
                break;

        }
    }
    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                getContext().getExternalFilesDir( Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }
    private void openCameraIntent() {
        Intent pictureIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        if(pictureIntent.resolveActivity(getContext().getPackageManager()) != null){
            //Create a file to store the image
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(),                                                                                                    "com.example.android.provider", photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        photoURI);
                startActivityForResult(pictureIntent,
                        REQUEST_CAPTURE_IMAGE);
            }
        }
    }
    private void guardarDatosFirebaseDialogNotDatabase(String platillo, String precio)  {
        final Map<String,Object> datos=new HashMap<>(  );
        final Map<String,Object> base_datos=new HashMap<>(  );
        final Map<String,Object> comentarios=new HashMap<>(  );
        FirebaseAuth mauth=FirebaseAuth.getInstance();
        FirebaseUser user = mauth.getCurrentUser();

        base_datos.put( "ciudad", address.get( 0 ).getAddressLine( 0 ) );
        base_datos.put( "direccion", address.get( 0 ).getLocality() );
        base_datos.put( "latitud", address.get( 0 ).getLatitude() );
        base_datos.put( "longitud", address.get( 0 ).getLongitude() );
        base_datos.put( "id_user", user.getUid() );
        comentarios.put( "id_comentarios",user.getUid() );
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
                    //UploadImage();
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

    public  void cargarProducto(){
        Toast.makeText( getContext(),"Error al cargar poductor",Toast.LENGTH_SHORT ).show();
        dialog =new Dialog( getContext() );
        dialog.setContentView( R.layout.dialog_eow );
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable( Color.TRANSPARENT ) );
        //findviewid
        dialog_et_precio= dialog.findViewById( R.id.dialog_edit_text_precio ) ;

        //Creating the instance of ArrayAdapter containing list of fruit names
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>
                (dialog.getContext(), android.R.layout.select_dialog_item, foods);
        //Getting the instance of AutoCompleteTextView
        acPlatillo = (AutoCompleteTextView) dialog.findViewById(R.id.acPlatillos);
        acPlatillo.setThreshold(1);//will start working from first character
        acPlatillo.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        acPlatillo.setTextColor(Color.RED);

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
        String adress=address.get( 0 ).getAddressLine( 0 ) ;
        String city=address.get( 0 ).getLocality(  ) ;
        dialogTextCiudad.setText( adress );
        dialogTextDireccion.setText( city );

        dialog.show();
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

//        if (resultCode == Activity.RESULT_OK ) {
//            Glide. with ( this ) .load ( imageFilePath ).centerCrop() .into ( dialog_iv_foto );
//        }
//        else if (resultCode == Activity.RESULT_CANCELED ) {
//            // El usuario canceló la acción
//        }
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

                                    geocoder= new Geocoder( getContext(), Locale.getDefault() );
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


        return false;
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();

    }
}
