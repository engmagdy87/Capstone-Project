package com.mm.plume;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.mm.plume.javaclasses.CurrentUser;

public class LoginActivity extends AppCompatActivity {
    SignInButton signInButton;
    ProgressBar progressBar;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;

    private static int RC_SIGN_IN = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        signInButton = findViewById(R.id.sign_in_button);
        progressBar = findViewById(R.id.pb_loading_indicator);

        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setStyle(SignInButton.SIZE_WIDE, SignInButton.COLOR_DARK);
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                signInButton.setEnabled(false);
                signIn();
            }
        });
    }


    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mGoogleSignInClient.revokeAccess();
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                progressBar.setVisibility(View.INVISIBLE);
                signInButton.setEnabled(true);
                Snackbar.make(findViewById(R.id.ll_login), "Invalid username or password, Try again", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            progressBar.setVisibility(View.INVISIBLE);
                            signInButton.setEnabled(true);

                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            Class destinationActivity = MainActivity.class;
                            Intent HomeActivity = new Intent(getBaseContext(), destinationActivity);
                            Bundle extras = new Bundle();
                            CurrentUser userInfo = new CurrentUser(currentUser.getUid().toString(), currentUser.getDisplayName(), currentUser.getEmail(), currentUser.getPhotoUrl().toString());
                            extras.putParcelable("currentUser", userInfo);
                            HomeActivity.putExtras(extras);
                            startActivity(HomeActivity);
                        } else {
                            // If sign in fails, display a message to the user.
                            progressBar.setVisibility(View.INVISIBLE);
                            signInButton.setEnabled(true);
                            Snackbar.make(findViewById(R.id.ll_login), "Invalid username or password, Try again", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Class destinationActivity = MainActivity.class;
            Intent HomeActivity = new Intent(getBaseContext(), destinationActivity);
            Bundle extras = new Bundle();
            CurrentUser userInfo = new CurrentUser(currentUser.getUid().toString(), currentUser.getDisplayName(), currentUser.getEmail(), currentUser.getPhotoUrl().toString());
            extras.putParcelable("currentUser", userInfo);
            HomeActivity.putExtras(extras);
            startActivity(HomeActivity);
        }
    }
}
