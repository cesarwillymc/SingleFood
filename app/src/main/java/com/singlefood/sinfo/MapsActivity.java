package com.singlefood.sinfo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.singlefood.sinfo.models.mapsFragment;

import java.io.ByteArrayOutputStream;

public class MapsActivity extends AppCompatActivity {
    NavigationView navigationView;
    DrawerLayout drawer;
    Toolbar tv_tolbar;
    ImageView image_header;
    private final int LOCATION = 1;
    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        FirebaseAuth mAuth= FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (Build.VERSION.SDK_INT > 16) {
            getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN );
        }
        requestPermissions( new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE},LOCATION);


        setContentView( R.layout.activity_maps );
        drawer = findViewById( R.id.drawer_layout );
        navigationView = findViewById( R.id.nav_view );
        setfragmetdefautl();
        drawer.addDrawerListener( new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        } );
        navigationView.setNavigationItemSelectedListener( new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Boolean transaccion=false;
                Fragment frag = null;
                switch (menuItem.getItemId()){
                    case R.id.menu_mapa:
                        frag= new mapsFragment();
                        transaccion = true;
                        break;
                    case R.id.menu_conten:
//                        frag= new informacionFragment();
//                        transaccion = true;
                        break;
                    case R.id.menu_distritos:
                        // frag= new distritos();
                        //transaccion = true;
                        break;
                    case R.id.menu_soon:
                        // frag= new Proximamente();
                        // transaccion = true;
                        break;
                    case R.id.SignOut:
                        FirebaseAuth.getInstance().signOut();
                        menuItem.setChecked( true );
                        drawer.closeDrawers();
                        break;
                }
                if(transaccion){
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace( R.id.content_frame,frag )
                            .commit();

                    menuItem.setChecked( true );

                    drawer.closeDrawers();
                }

                return false;
            }
        } );
        setTolbar();
        image_header =(ImageView) findViewById( R.id.image_header );
        //imagetoNavegationDrawer(  );
    }

    private void imagetoNavegationDrawer( ) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fondo01 );
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        //decode base64 string to image
        imageBytes = Base64.decode(imageString, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        image_header.setImageBitmap(decodedImage);
    }

    private void setTolbar() {
        tv_tolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.wd_toolvar);
        setSupportActionBar( tv_tolbar );
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                drawer.openDrawer( GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void  setfragmetdefautl(){
        getSupportFragmentManager()
                .beginTransaction()
                .replace( R.id.content_frame,new mapsFragment() )
                .commit();
        MenuItem menuItem= navigationView.getMenu().getItem( 0);
        menuItem.setChecked( true );
        drawer.closeDrawers();
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById( R.id.drawer_layout );
        if (drawer.isDrawerOpen( GravityCompat.START )) {
            drawer.closeDrawer( GravityCompat.START );
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                String permission = permissions[0];
                int result = grantResults[0];
                if (permission.equals( Manifest.permission.ACCESS_FINE_LOCATION )  ) {
                    if (result == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText( MapsActivity.this, "Permiso Activado", Toast.LENGTH_LONG );

                    } else {
                        Toast.makeText( MapsActivity.this, "Permiso Denegado", Toast.LENGTH_LONG );
                    }
                }
                break;

            default:
                super.onRequestPermissionsResult( requestCode, permissions, grantResults );
        }
    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}




/*
public class MapsActivity extends FragmentActivity implements    GoogleMap.OnMarkerDragListener,OnMapReadyCallback, GoogleMap.OnMyLocationClickListener, GoogleMap.OnMyLocationButtonClickListener, View.OnClickListener
{
    private GoogleMap mMap;
    FloatingActionButton fab;
    private List<String> nombres;
    private int counter=0;
    List<Address> adres;
    private RecyclerView Rview;
    private  Dialog dialog;
    Geocoder geo;
    Marker m;
    private RecyclerView.Adapter adapterRview;
    private RecyclerView.LayoutManager layourRview;
    DatabaseReference mdatareference;
    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_maps );
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById( R.id.map );
        mapFragment.getMapAsync( this );
        mdatareference= FirebaseDatabase.getInstance().getReference();
        this.nombres = getAllnames();
        fab= findViewById( R.id.floatingbar_save );
        Rview = findViewById(R.id.my_recycler_view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions( new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
        layourRview = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        adapterRview = new MyAdapter(nombres, R.layout.list_single_card, new MyAdapter.OnItemClickListener() {
            @Override
            public void OnClickListener(String name, int position) {
                borrar(position);

            }
        });
        fab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(adres==null){
                    Toast.makeText( MapsActivity.this, "Mueva el cursos por favor", Toast.LENGTH_SHORT ).show();
                }
                else {
//                    String adress=adres.get( 0 ).getAddressLine( 0 ) ;
//                    String city=adres.get( 0 ).getLocality(  ) ;
//                    new AlertDialog.Builder(MapsActivity.this)
//                            .setTitle( "Desea guardar en Guardar")
//                            .setMessage( ""+ "direccion: "+adress+"\n"+ "ciudad: "+city+"\n"  )
//                            .setPositiveButton( "Si", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    guardarLatitudLongitud();
//                                }
//                            } )
//                            .setNegativeButton( "No",null )
//                            .show();
                    cargarProducto( "Hola",15.2 );
                }

            }
        } );
        Rview.setLayoutManager(layourRview);
        Rview.setAdapter(adapterRview);


    }
    private void guardarLatitudLongitud() {

        String adress=adres.get( 0 ).getAddressLine( 0 ) ;
        String city=adres.get( 0 ).getLocality(  ) ;
        double latitud=adres.get( 0 ).getLatitude() ;
        double longitud=adres.get( 0 ).getLongitude(  ) ;
        Map<String,Object> latlang=new HashMap<>(  );
        latlang.put( "latitud",latitud );
        latlang.put( "longitud",longitud );
        latlang.put( "direccion",adress );
        latlang.put( "ciudad",city );
        mdatareference.child( "tachos" ).push().setValue( latlang );


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled( true );
        mMap.setOnMarkerDragListener(this);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        mMap.getUiSettings().setCompassEnabled( false );
        mMap.getUiSettings().setIndoorLevelPickerEnabled( false );

        geo= new Geocoder( MapsActivity.this, Locale.getDefault() );
        LatLng dato = new LatLng( -16.387378,-71.5421 );
        MarkerOptions a = new MarkerOptions().position(dato).title( "Actual" ).draggable( true ) ;
        m = mMap.addMarker(a);
        mMap.moveCamera( CameraUpdateFactory.newLatLng( dato ) );
        CameraUpdate zoom= CameraUpdateFactory.zoomTo( 15 );
        mMap.animateCamera( zoom );

    }

    private List<String> getAllnames(){
        return new ArrayList<String>(){{
            add("Elias");
            add("Antonio");
            add("Cesar");
            add("Willy");
        }};
    }


    private void agregar(int position){
        this.nombres.add(position,"agregado: "+ (++counter));
        this.adapterRview.notifyItemInserted(position);
        layourRview.scrollToPosition(position);
    }
    private void borrar(int position){
        this.nombres.remove(position);
        this.adapterRview.notifyItemRemoved(position);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                String permission = permissions[0];
                int result = grantResults[0];
                if (permission.equals( Manifest.permission.ACCESS_FINE_LOCATION )  ) {
                    if (result == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText( MapsActivity.this, "Permiso Activado", Toast.LENGTH_LONG );

                    } else {
                        Toast.makeText( MapsActivity.this, "Permiso Denegado", Toast.LENGTH_LONG );
                    }
                }
                break;

            default:
                super.onRequestPermissionsResult( requestCode, permissions, grantResults );
        }
    }
    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Primero", Toast.LENGTH_SHORT).show();
        LatLng dato2 = new LatLng( location.getLatitude(), location.getLongitude() );
        m.setPosition( dato2 );
        mMap.moveCamera( CameraUpdateFactory.newLatLng( dato2) );
        CameraUpdate zoom= CameraUpdateFactory.zoomTo( 20 );
        mMap.animateCamera( zoom );


    }
    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "Segundo", Toast.LENGTH_SHORT).show();
        return false;

    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        double latitud = marker.getPosition().latitude;
        double longitud = marker.getPosition().longitude;
        try {
            adres=geo.getFromLocation( latitud,longitud,1 );
        } catch (IOException e) {
            e.printStackTrace();
        }

        String adress=adres.get( 0 ).getAddressLine( 0 ) ;
        String city=adres.get( 0 ).getLocality(  ) ;
        String state=adres.get( 0 ).getAdminArea( ) ;
        String country=adres.get( 0 ).getCountryName(  ) ;
        String postal=adres.get( 0 ).getPostalCode(  ) ;
        double latitud2=adres.get( 0 ).getLatitude() ;
        double longitud3=adres.get( 0 ).getLongitude(  ) ;

        Toast.makeText( MapsActivity.this,"Latitud: "+ latitud2+ "\n" +
                "Longitud: "+longitud3+"\n"+
                "City: "+city+"\n"+
                "State: "+state+"\n"+
                "Country: "+country+"\n"+
                "PostalCode: "+postal,Toast.LENGTH_SHORT ).show();

    }
    public  void cargarProducto(String nombre,Double precio){
        dialog =new Dialog( this );
        dialog.setContentView( R.layout.dialog_eow );
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable( Color.TRANSPARENT ) );
        dialog.show();
        dialogButtonsi=findViewById(  )
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.dialog_no:
                dialog.dismiss();
                break;
            case R.id.dialog_yes:
                dialog.dismiss();
                break;
        }
    }
}



*/