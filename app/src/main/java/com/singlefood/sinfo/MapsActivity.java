package com.singlefood.sinfo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.singlefood.sinfo.models.fragMapaPrincipal;

public class MapsActivity extends AppCompatActivity {
    NavigationView navigationView;
    DrawerLayout drawer;
    //Toolbar tv_tolbar;
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
                        frag= new fragMapaPrincipal();
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
        //setTolbar();
        image_header =(ImageView) findViewById( R.id.image_header );
        //imagetoNavegationDrawer(  );
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
                .replace( R.id.content_frame,new fragMapaPrincipal() )
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



