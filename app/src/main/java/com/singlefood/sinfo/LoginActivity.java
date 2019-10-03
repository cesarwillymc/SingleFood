package com.singlefood.sinfo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnRegistrar;
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference mDatabase;

    @BindView( R.id.progress_bar_login)



    @BindView( R.id.progress_bar )
    ProgressBar progressBar;
    @BindView( R.id.toolbar_general )
    Toolbar toolbar_general;
    @BindView(R.id.etEmailUser_login)
    EditText emaillogin;
    @BindView(R.id.etNameUser_login)
    EditText nombre;
    @BindView(R.id.etPhoneUser_login)
    EditText phone;
    @BindView(R.id.ivPhotoUser_login)
    ImageView imagen;
    @BindView(R.id.btnRegisterUser_login)
    Button logeo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        mAuth=FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference();
        user=mAuth.getCurrentUser();
        setContentView( R.layout.activity_login );
        ButterKnife.bind( this );
        configToolbar();
        logeo.setOnClickListener(this);

    }


    private void configToolbar() {
        setSupportActionBar( toolbar_general );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }


    private void updateUI(FirebaseUser user){
        if(user != null){

        finish();
        }

    }



    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btnRegisterUser_login:
                String nombre_string= nombre.getText().toString().trim();
                String email_string= emaillogin.getText().toString().trim();
                String telefono_string= phone.getText().toString().trim();
                if (TextUtils.isEmpty(nombre_string)||TextUtils.isEmpty(email_string)||TextUtils.isEmpty(telefono_string)){
                    Toast.makeText(this,"Completa los datos por favor!!",Toast.LENGTH_LONG).show();
                }else {
                    progressBar.setVisibility(View.VISIBLE);
                    IniciarSesion(nombre_string,email_string,telefono_string);
                }

                break;

        }
    }

    private void IniciarSesion(String nombre_string, String email_string, String telefono_string) {
        mAuth.signInWithEmailAndPassword(email_string, telefono_string)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("tag", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            progressBar.setVisibility( View.GONE );
                            updateUI(user);
                        } else {
                            crearcuenta(nombre_string,email_string,telefono_string);

                        }

                        // ...
                    }

                }).addOnFailureListener(new OnFailureListener() {

                });

    }

    private void crearcuenta(String nombre_string, String email_string, String telefono_string) {
        mAuth.createUserWithEmailAndPassword(email_string, telefono_string)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();


                            Map<String,Object> users = new HashMap<>();
                            users.put("nombre",nombre_string);
                            users.put("email",email_string);
                            users.put("phone",telefono_string);
                            users.put("imagen","hola");

                            mDatabase.child("usuarios_single").child(task.getResult().getUser().getUid()).setValue(users);
                            progressBar.setVisibility( View.GONE );
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility( View.GONE );
                            updateUI(null);
                        }

                        // ...
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),""+e,Toast.LENGTH_LONG).show();
            }
        });
    }



}
