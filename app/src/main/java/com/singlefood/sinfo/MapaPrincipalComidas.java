package com.singlefood.sinfo;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
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
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.singlefood.sinfo.models.informacion_platillos;
import com.singlefood.sinfo.models.productos.RecyclerProductoAdapter;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

// classes needed to initialize map
// classes needed to add the location component
// classes needed to add a marker
// classes to calculate a route
// classes needed to launch navigation UI


public class MapaPrincipalComidas extends Fragment implements OnMapReadyCallback,MapboxMap.OnMarkerClickListener, PermissionsListener, View.OnClickListener {
    // variables for adding location layer
    private MapView mapView;
    private MapboxMap mapboxMap;
    // variables for adding location layer
    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;
    // variables for calculating and drawing a route
    private DirectionsRoute currentRoute;
    private static final String TAG = "DirectionsActivity";
    private NavigationMapRoute navigationMapRoute;
    // variables needed to initialize navigation

    private ImageButton button;
    private Context context;

    private final int TAKEFOTO=1;
    private final int GPS=51;
    private Uri fileImage;
    final HashMap<Long, String> markerMapPlatillos = new HashMap<Long, String>();
    //botones principales del mapa
    private FloatingActionButton fab_collapse;
    private FloatingActionButton fab_hidden;
    private FloatingActionButton fab_go;
    private AutoCompleteTextView acBuscadorPlatillo;
    private AutoCompleteTextView acPlatillo;

    BottomSheetBehavior bottomSheetBehavior;
    private ProgressDialog progressDialog;
    private EditText dialog_et_precio;
    private EditText dialog_et_direccion;
    private RatingBar ratingBar_dialog;
    private EditText dialog_comentario;
    Button dialogButtonsi;
    private FusedLocationProviderClient fusedLocationProviderClient; //Ultima Ubicacion
    private LocationCallback locationCallback; //ACtualizar posicion
    private Location location;
    private Geocoder geocoder;

    private StorageReference mStorageReference;
    private DatabaseReference mDatabase; //FIREBASE

    private RecyclerView rvListaPlatillos;
    private List<Address> address;
    private RecyclerView.LayoutManager layourRview;
    private RecyclerView.Adapter adapterRVTarjetaPlatillo;

    private ImageView dialog_iv_foto;
    private Bitmap bitmap;
    ArrayList<platillos> arrayListPlatillos= new ArrayList<>();

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        Mapbox.getInstance(container.getContext(), getString(R.string.access_token));
        context = getContext();

        //cargamos el mapa principal
        view = inflater.inflate( R.layout.fra_mapa_principal, container, false );
        mapView = view.findViewById(R.id.mapView);
        if (mapView != null) {
            mapView.onCreate( savedInstanceState );
            mapView.onResume();
            mapView.getMapAsync( this );
        }

        context = getContext();


        gpsEnable();
        rvListaPlatillos = view.findViewById(R.id.rvListaPlatillos);
        layourRview = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvListaPlatillos.setLayoutManager(layourRview);

        mStorageReference= FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference(); //Instanciar BD Firebase

        //Solicitamos permisos de ubicacion para versiones anteriores
        /*if (ActivityCompat.checkSelfPermission(container.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {

            new AlertDialog.Builder( container.getContext() )
                    .setTitle( "Activa Permiso" )
                    .setMessage( "Permiso desabilitado!! " )
                    .setPositiveButton( "Si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final Intent i = new Intent();
                            i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            i.addCategory(Intent.CATEGORY_DEFAULT);
                            i.setData(Uri.parse("package:" + container.getContext().getPackageName()));
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                            container.getContext().startActivity(i);
                        }
                    } )
                    .setNegativeButton( "No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            if (Build.VERSION.SDK_INT >= 26) {
                                ft.setReorderingAllowed(false);
                            }
                            ft.detach(MapaPrincipalComidas.this).attach(MapaPrincipalComidas.this).commit();
                        }
                    } )
                    .show();
        }
*/
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.crear_comida_dialog);
        bottomSheetBehavior = BottomSheetBehavior.from(linearLayout);
        fab_collapse = (FloatingActionButton) view.findViewById( R.id.fab_collapse_dialog );
        fab_hidden = (FloatingActionButton) view.findViewById( R.id.fab_hidden_dialog );

        fab_collapse.setOnClickListener(this );
        fab_hidden.setOnClickListener( this );


        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
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

                //mMap.animateCamera( CameraUpdateFactory.newLatLngZoom( new LatLng( location.getLatitude(),location.getLongitude()),14 ));
                Toast.makeText( getContext(),"Disfrute de estas delicias!!" , Toast.LENGTH_SHORT ).show();
            }
        });

        return view;
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(getString(R.string.navigation_guidance_day), new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        enableLocationComponent(style);
                        button = view.findViewById(R.id.startButton);

                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                        button.setVisibility(View.GONE);
                                        if (currentRoute==null)
                                            button.setVisibility(View.VISIBLE);
                                        else
                                            button.setVisibility(View.GONE);
                                        boolean simulateRoute = false;
                                        NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                                                .directionsRoute(currentRoute)
                                                .shouldSimulateRoute(simulateRoute)
                                                .build();

                                }
                            });

                        }

                        });

                        mDatabase.child("platillos").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                ArrayList<String> llaves = new ArrayList<>();
                                int i = 0;
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    platillos platillosc = snapshot.getValue(platillos.class);
                                    Double latitud = platillosc.getPlaces().getLatitud();
                                    Double longitud = platillosc.getPlaces().getLongitud();


                                    // Create an Icon object for the marker to use
                                    IconFactory iconFactory = IconFactory.getInstance(context);
                                    Icon icon;

                                    if (i++ % 2 == 0)
                                        icon = iconFactory.fromResource(R.drawable.ico_marker_meat);
                                    else
                                        icon = iconFactory.fromResource(R.drawable.ico_marker_fish);

                                    // Add the marker to the map
                                    Marker marcador = mapboxMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(latitud, longitud))
                                            .title(snapshot.getKey())
                                            .icon(icon));


                                    llaves.add(snapshot.getKey());
                                    arrayListPlatillos.add(platillosc);
                                    mapboxMap.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
                                        @Override
                                        public boolean onMarkerClick(@NonNull Marker marker) {
                                            for (int i = 0; i < arrayListPlatillos.size(); i++)
                                                if (llaves.get(i).equals(marker.getTitle())) {
                                                    rvListaPlatillos.smoothScrollToPosition(i);
                                                    Point destinationPoint = Point.fromLngLat(arrayListPlatillos.get(i).getPlaces().getLongitud(), arrayListPlatillos.get(i).getPlaces().getLatitud());
                                                    Point originPoint = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
                                                            locationComponent.getLastKnownLocation().getLatitude());
                                                    getRoute(originPoint, destinationPoint);
                                                }

                                            return true;
                                        }
                                    });

                                }


                                adapterRVTarjetaPlatillo = new RecyclerProductoAdapter(getContext(), R.layout.rv_tarjeta_platillo, arrayListPlatillos, new RecyclerProductoAdapter.OnItemClickListener() {
                                    @Override
                                    public void OnClickListener(platillos platillos, int position) {

                                        Intent i = new Intent(getActivity(), informacion_platillos.class);
                                        i.putExtra("key", llaves.get(position));
                                        startActivity(i);

                                    }
                                });

                                rvListaPlatillos.setAdapter(adapterRVTarjetaPlatillo);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

        }


    private void addDestinationIconSymbolLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addImage("destination-icon-id",
                BitmapFactory.decodeResource(this.getResources(), R.drawable.mapbox_marker_icon_default));
        GeoJsonSource geoJsonSource = new GeoJsonSource("destination-source-id");
        loadedMapStyle.addSource(geoJsonSource);
        SymbolLayer destinationSymbolLayer = new SymbolLayer("destination-symbol-layer-id", "destination-source-id");
        destinationSymbolLayer.withProperties(
                iconImage("destination-icon-id"),
                iconAllowOverlap(true),
                iconIgnorePlacement(true)
        );
        loadedMapStyle.addLayer(destinationSymbolLayer);

    }

   /* @Override
    public boolean onMapClick(@NonNull LatLng point) {

        /*Point destinationPoint = Point.fromLngLat(point.getLongitude(), point.getLatitude());
        Point originPoint = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
                locationComponent.getLastKnownLocation().getLatitude());

        GeoJsonSource source = mapboxMap.getStyle().getSourceAs("destination-source-id");
        if (source != null) {
            source.setGeoJson(Feature.fromGeometry(destinationPoint));
        }

        getRoute(originPoint, destinationPoint);

        return true;
    }*/

    private void getRoute(Point origin, Point destination) {
        NavigationRoute.builder(context)
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
// You can get the generic HTTP info about the response
                        Log.d(TAG, "Response code: " + response.code());
                        if (response.body() == null) {
                            Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                            return;
                        } else if (response.body().routes().size() < 1) {
                            Log.e(TAG, "No routes found");
                            return;
                        }

                        currentRoute = response.body().routes().get(0);

// Draw the route on the map
                        if (navigationMapRoute != null) {
                            navigationMapRoute.removeRoute();
                        } else {
                            navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);
                        }
                        navigationMapRoute.addRoute(currentRoute);
                        button.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                        Log.e(TAG, "Error: " + throwable.getMessage());
                    }
                });
        //button.setVisibility(View.VISIBLE);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(context, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationComponent(mapboxMap.getStyle());
        } else {
            Toast.makeText(context, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            //mapView.finish();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
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
            if (adress.contains("null")||adress.contains("unde")){
                dialog_et_direccion.setText( "" );
                dialog_et_direccion.setHint( "Introduzca Direccion" );
            }else{
                dialog_et_direccion.setText( adress+" barrio " );
            }

        }
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
        base_datos.put( "idUser", user.getUid() );
        comentarios.put( "idComentarios",user.getUid() );
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
            datos.put("idUser",user.getUid());
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

    @SuppressLint("MissingPermission")
    private void getDeviceLocation() {
        fusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener( new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()){
                            location= task.getResult();
                            if(location!=null){

//                                mMap.moveCamera( CameraUpdateFactory.newLatLngZoom( new LatLng( location.getLatitude(),location.getLongitude() ),18) );


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

                                        //mMap.moveCamera( CameraUpdateFactory.newLatLngZoom( new LatLng( location.getLatitude(),location.getLongitude() ),18) );

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
    public boolean onMarkerClick(@NonNull Marker marker) {
        return false;
    }
    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(Style style) {

        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(getContext())) {

            // Get an instance of the component
             locationComponent = mapboxMap.getLocationComponent();

            // Activate with a built LocationComponentActivationOptions object
            locationComponent.activateLocationComponent(LocationComponentActivationOptions.builder(getContext(), style).build());

            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);

        } else {

            permissionsManager = new PermissionsManager(this);

            permissionsManager.requestLocationPermissions(getActivity());

        }
    }

}