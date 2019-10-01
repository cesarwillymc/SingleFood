package com.singlefood.sinfo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    EditText txtNombre, txtApellido, txtUsuario,txtContraseña;
    Spinner spnTipoUser;
    Button btnRegistrar;
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference mDatabase;
    CallbackManager mCallbackManager;
    LoginButton loginButtonFacebook;
    private  String mVerificationId;
//    @BindView( R.id.login_button_email )
//    Button loginButtonEmail;
//    @BindView( R.id.login_button_phone )
//    Button loginButtonPhone;
//    @BindView( R.id.login_edit_text_email )
//    EditText login_mail_email;
//    @BindView( R.id.login_edit_text_password )
//    EditText login_mail_password;
//    @BindViews( {R.id.login_edit_text_email,R.id.login_edit_text_password} )
//    List<View> accesorios_mail;
//    @BindView( R.id.login_button_phone_code )
//    Button login_phone_button_code;
//    @BindView( R.id.login_edit_text_phone_code )
//    EditText login_phone_et_code;
//    @BindViews( {R.id.login_button_phone_code,R.id.login_edit_text_phone_code} )
//    List<View> accesorios_phone;

    @BindView( R.id.progress_bar )
    ProgressBar progressBar;
    @BindView( R.id.toolbar_general )
    Toolbar toolbar_general;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        setContentView( R.layout.activity_login );
        ButterKnife.bind( this ,LoginActivity.this);
//        visibilidadFuncion(accesorios_mail,View.GONE);
//        visibilidadFuncion(accesorios_phone,View.GONE);
        configToolbar();
//        FacebookSdk.sdkInitialize(getApplicationContext());
//        AppEventsLogger.activateApp(this);
        mCallbackManager = CallbackManager.Factory.create();
//        loginButtonFacebook = findViewById(R.id.login_facebook_button);
//        loginButtonFacebook.setReadPermissions("email", "public_profile");
//        loginButtonFacebook.setOnClickListener( this );
//        loginButtonEmail.setOnClickListener( this );
//        loginButtonPhone.setOnClickListener( this );
//        login_phone_button_code.setOnClickListener( this );

//        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                Log.d("tag", "facebook:onSuccess:" + loginResult);
//                handleFacebookAccessToken(loginResult.getAccessToken());
//            }
//
//            @Override
//            public void onCancel() {
//                Log.d("tag", "facebook:onCancel");
//                // ...
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//                Log.d("tag", "facebook:onError", error);
//                // ...
//            }
//        });// ...



    }

    private void visibilidadFuncion(List<View> funcion, int visible) {
        for (View vistas:funcion){
            vistas.setVisibility( visible );
        }
    }

    private void configToolbar() {
        setSupportActionBar( toolbar_general );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        //loginButtonPhone.setEnabled( false );
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
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    private void  handleFacebookAccessToken(AccessToken token) {
        Log.d("Tag", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("tag", "signInWithCredential:success");
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("tag", "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }


    private void updateUI(FirebaseUser user){
        if(user != null){

        finish();
        }

    }
    public void iniciarSesion_email(String email,String password){

        mAuth.signInWithEmailAndPassword(email, password)
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
                            // If sign in fails, display a message to the user.
                            Log.w("tag", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility( View.GONE );
                            updateUI(null);
                        }

                        // ...
                    }
                });

    }
    public void IniciarSesion(){
        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                       Toast.makeText( LoginActivity.this,"error cancelado",Toast.LENGTH_SHORT ).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText( LoginActivity.this,"error "+exception,Toast.LENGTH_SHORT ).show();

                    }
                });


    }
    public void requestCodeSms(String Phone){
        if(TextUtils.isEmpty( Phone )){
            return;
        }
        PhoneAuthProvider.getInstance().verifyPhoneNumber( Phone, 60, TimeUnit.SECONDS, this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithCredencial(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText( LoginActivity.this,"error "+ e.getMessage(),Toast.LENGTH_SHORT ).show();
                progressBar.setVisibility( View.GONE );
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent( s, forceResendingToken );
                mVerificationId=s;
//                login_phone_et_code.setHint( "Ingrese codigo" );
//                loginButtonPhone.setEnabled( true );
//                login_phone_button_code.setEnabled( false );
//                login_phone_et_code.setText( "" );
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                super.onCodeAutoRetrievalTimeOut( s );

                Toast.makeText( LoginActivity.this,"OncodeRetrievalTimeOut "+ s,Toast.LENGTH_SHORT ).show();
                progressBar.setVisibility( View.GONE );
            }
        } );
    }

    private void signInWithCredencial(PhoneAuthCredential phoneAuthCredential) {
        mAuth.signInWithCredential( phoneAuthCredential )
                .addOnCompleteListener( this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();
                            progressBar.setVisibility( View.GONE );
                            Toast.makeText( LoginActivity.this,"completado ",Toast.LENGTH_SHORT ).show();
                            updateUI(user);
                        }else {
                            Toast.makeText( LoginActivity.this,"fallo en la credencial ",Toast.LENGTH_SHORT ).show();

                        }
                    }
                } );
    }
    public void SignInSMSPhone(String codePhone){
        if(TextUtils.isEmpty( codePhone )){
            return;
        }
        signInWithCredencial( PhoneAuthProvider.getCredential( mVerificationId,codePhone ) );
    }
    public void registrar(){



        final String name = txtNombre.getText().toString().trim();
        final String last = txtApellido.getText().toString().trim();
        String pass = txtContraseña.getText().toString().trim();
        final String user_email = txtUsuario.getText().toString().trim();
        final String type = spnTipoUser.getSelectedItem().toString().trim();

        mAuth.createUserWithEmailAndPassword(user_email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();


                            Map<String,Object> users = new HashMap<>();
                            users.put("Nombre",name);
                            users.put("Apellidos",last);
                            users.put("email",user_email);
                            users.put("tipo",type);

                            mDatabase.child("Usuarios").child(task.getResult().getUser().getUid()).setValue(users);

                            updateUI(user);



                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
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

    @Override
    public void onClick(View view) {
        switch(view.getId()){
//            case R.id.login_facebook_button:
//                IniciarSesion();
//                break;
//            case R.id.login_button_email:
//                //String text=loginButtonEmail.getText().toString().trim();
//                if(("".equals(  login_mail_email.getText().toString().trim())||login_mail_email.getText().toString().trim()==null)||("".equals(  login_mail_password.getText().toString().trim())||login_mail_password.getText().toString().trim()==null)){
////                    loginButtonEmail.setText( "Login" );
////                    login_mail_email.setVisibility( View.VISIBLE );
////                    login_mail_email.setVisibility( View.VISIBLE );
////                    login_phone_et_code.setVisibility( View.GONE );
////                    login_phone_button_code.setVisibility( View.GONE );
////                    visibilidadFuncion(accesorios_phone,View.GONE);
////                    visibilidadFuncion(accesorios_mail,View.VISIBLE);
//                    Toast.makeText( this,"Error vacio", Toast.LENGTH_SHORT ).show();
//
//                }else {
////
////                    progressBar.setVisibility( View.VISIBLE );
//                    iniciarSesion_email(login_mail_email.getText().toString(),login_mail_password.getText().toString().trim());
////                    loginButtonEmail.setText( R.string.sign_in_with_gmail);
////                    login_mail_email.setVisibility( View.GONE );
////                    login_mail_email.setVisibility( View.GONE );
////                    login_phone_et_code.setVisibility( View.VISIBLE );
////                    login_phone_button_code.setVisibility( View.VISIBLE );
//                }
//
//                break;
//            case R.id.login_button_phone:
//               // String texto=loginButtonPhone.getText().toString().trim();
//                if(("".equals(  login_phone_et_code.getText().toString().trim())||login_phone_et_code.getText().toString().trim()==null)){
//                    Toast.makeText( this,"Error vacio", Toast.LENGTH_SHORT ).show();
//                }else {
//
//                    progressBar.setVisibility( View.VISIBLE );
//                    SignInSMSPhone(login_phone_et_code.getText().toString().trim() );
//                }
//                break;
//            case R.id.login_button_phone_code:
//
//                if(("".equals(  login_phone_et_code.getText().toString().trim())||login_phone_et_code.getText().toString().trim()==null)){
//                    Toast.makeText( this,"Error vacio", Toast.LENGTH_SHORT ).show();
//                }else {
//
//                    progressBar.setVisibility( View.VISIBLE );
//                    requestCodeSms(login_phone_et_code.getText().toString().trim()  );
//
//                }
//
//                break;
        }
    }
}
