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
import com.singlefood.sinfo.View.fragments.MapaPrincipalComidas;

public class MapsActivity extends AppCompatActivity {
    NavigationView ap_nav_view;
    DrawerLayout ap_drawer;
    ImageView ap_image_view;
    private final int LOCATION = 1;
    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate( savedInstanceState );

        if (Build.VERSION.SDK_INT > 16) {
            getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN );
        }
        requestPermissions( new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE},LOCATION);

        setContentView( R.layout.actividad_principal);
        ap_drawer = findViewById( R.id.ap_drawer_layout_main );
        ap_nav_view = findViewById( R.id.ap_nav_view );
        setfragmetdefautl();
        ap_drawer.addDrawerListener( new DrawerLayout.DrawerListener() {
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
        ap_nav_view.setNavigationItemSelectedListener( new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Boolean transaccion=false;
                Fragment frag = null;
                switch (menuItem.getItemId()){
                    case R.id.menu_mapa:
                        frag= new MapaPrincipalComidas();
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
                        ap_drawer.closeDrawers();
                        break;
                }
                if(transaccion){
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace( R.id.content_frame,frag )
                            .commit();

                    menuItem.setChecked( true );

                    ap_drawer.closeDrawers();
                }

                return false;
            }
        } );
        ap_image_view =(ImageView) findViewById( R.id.lnd_image_view_header );
    }

    //abre el open drawer
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                ap_drawer.openDrawer( GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //selecciona fragmento por defecto
    void  setfragmetdefautl(){
        getSupportFragmentManager()
                .beginTransaction()
                .replace( R.id.content_frame,new MapaPrincipalComidas() )
                .commit();
        MenuItem menuItem= ap_nav_view.getMenu().getItem( 0);
        menuItem.setChecked( true );
        ap_drawer.closeDrawers();
    }
    //gestos
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById( R.id.ap_drawer_layout_main );
        if (drawer.isDrawerOpen( GravityCompat.START )) {
            drawer.closeDrawer( GravityCompat.START );
        } else {
            super.onBackPressed();
        }
    }
    //permisos para mapa 
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



