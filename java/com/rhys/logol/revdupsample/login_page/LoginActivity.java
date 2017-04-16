package com.rhys.logol.revdupsample.login_page;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rhys.logol.revdupsample.R;
import com.rhys.logol.revdupsample.main_page.Feed.ShowImagesActivity;
import com.rhys.logol.revdupsample.main_page.FragmentManager.FragmentManager;
import com.rhys.logol.revdupsample.users.User;


// A login screen that offers login via email/password.

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, LoginView {

    private static final String TAG = "LoginActivity";

    private EditText mEmailField;
    private EditText mPasswordField;

    private Button createSubmitButton;

    public ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public FirebaseUser user;

    public String userId;

    private LoginPresenter presenter;

    private Boolean navHomeToggle = true;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Views
        mEmailField = (EditText) findViewById(R.id.field_email);
        mPasswordField = (EditText) findViewById(R.id.field_password);

        // Buttons
        Button submitButton = (Button) findViewById(R.id.submitButton);
        createSubmitButton = (Button) findViewById(R.id.createSubmitButton);
        findViewById(R.id.email_sign_in_button).setOnClickListener(this);
        findViewById(R.id.email_create_account_button).setOnClickListener(this);

        // Progress Bars
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    userId = user.getUid();
                    if(navHomeToggle) {
                        navigateToHome();
                    }
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
        presenter = new LoginPresenterImpl(this);
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        presenter.validateCredentials(email, password);
        if(TextUtils.isEmpty(email)) return;
        if(TextUtils.isEmpty(password)) return;

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                if (!task.isSuccessful()) {
                    Log.w(TAG, "signInWithEmail:failed", task.getException());
                    setUsernameError();
                    setPasswordError();
                    hideProgress();
                    showKeyboard();
                }
            }
        });
    }

    private void createAccount(final String firstName, final String lastName, final String email, String password) {
        Log.d(TAG, "createAccount:" + email);

        mAuth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                if (!task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, R.string.auth_failed, Toast.LENGTH_SHORT).show();
                    hideProgress();
                }else {
                    try {
                        mAuth.getCurrentUser().sendEmailVerification();
                        Toast.makeText(LoginActivity.this, "Please Check Inbox", Toast.LENGTH_SHORT).show();
                        //For now we will make sure the new user is logged in our database
                        writeNewUser(mAuth.getCurrentUser().getUid(), firstName, lastName, email);
                        navigateToHome();
                    } catch (NullPointerException e) {
                        Toast.makeText(LoginActivity.this, "Email Verification Failed", Toast.LENGTH_SHORT).show();
                        hideProgress();
                    }
                }
            }
        });
    }

    private void writeNewUser(String userId, String firstName, String lastName, String email) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        User user = new User(firstName,lastName,email);

        mDatabase.child("users").child(userId).setValue(user);
    }

    private void setResetDialog(){
        // Create custom dialog object
        final Dialog dialog = new Dialog(LoginActivity.this);
        // Include reset_password_dialog.xml file
        dialog.setContentView(R.layout.reset_password_dialog);
        // Set dialog title
        dialog.setTitle("Reset Password Dialog");

        dialog.show();

        Button submitButton = (Button) dialog.findViewById(R.id.submitButton);

        final EditText mEmailDialog = (EditText) dialog.findViewById(R.id.username);

        // if decline button is clicked, close the custom dialog
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(mEmailDialog.getText().toString())){
                    mEmailDialog.setError("Required.");
                }else{
                    mEmailDialog.setError(null);
                    mAuth.sendPasswordResetEmail(mEmailDialog.getText().toString());
                    Toast.makeText(LoginActivity.this, "Successfully sent.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });
    }

    public void setAccountDialog(){
        // Create custom dialog object
        final Dialog createDialog = new Dialog(LoginActivity.this);
        // Include reset_password_dialog.xml file
        createDialog.setContentView(R.layout.create_account_dialog);
        // Set dialog title
        createDialog.setTitle("Create Account Dialog");

        createDialog.show();

        createSubmitButton = (Button) createDialog.findViewById(R.id.createSubmitButton);

        final EditText createFirst = (EditText) createDialog.findViewById(R.id.first_name);
        final EditText createLast = (EditText) createDialog.findViewById(R.id.last_name);
        final EditText createEmail = (EditText) createDialog.findViewById(R.id.create_email);
        final EditText createPassword = (EditText) createDialog.findViewById(R.id.create_password);
        final EditText createConfirmPassword = (EditText) createDialog.findViewById(R.id.Create_confirm_password);

        final boolean[] first = {false};
        final boolean[] last = {false};
        final boolean[] email = {false};
        final boolean[] pass = {false};
        final boolean[] conPass = {false};

        createSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(createFirst.getText().toString())){
                    createFirst.setError("Required.");
                    first[0] = false;
                }else{
                    createFirst.setError(null);
                    first[0] = true;
                }if(TextUtils.isEmpty(createLast.getText().toString())){
                    createLast.setError("Required.");
                    last[0] = false;
                }else{
                    createLast.setError(null);
                    last[0] = true;
                }if(TextUtils.isEmpty(createEmail.getText().toString())){
                    createEmail.setError("Required.");
                    email[0] = false;
                }else{
                    createEmail.setError(null);
                    email[0] = true;
                }if(TextUtils.isEmpty(createPassword.getText().toString())){
                    createPassword.setError("Required.");
                    pass[0] = false;
                }else{
                    createPassword.setError(null);
                    pass[0] = true;
                }if(TextUtils.isEmpty(createConfirmPassword.getText().toString())){
                    createConfirmPassword.setError("Required.");
                    conPass[0] = false;
                }else{
                    createConfirmPassword.setError(null);
                    if(createConfirmPassword.getText().toString().equals(createPassword.getText().toString())) {
                        createConfirmPassword.setError(null);
                        conPass[0] = true;
                    }else{
                        createConfirmPassword.setError("Incorrect.");
                    }
                }if(first[0] && last[0] && email[0] && pass[0] && conPass[0]){
                    createAccount(createFirst.getText().toString(),createLast.getText().toString(),createEmail.getText().toString(), createPassword.getText().toString());
                    createDialog.dismiss();
                }
            }
        });
    }

    @Override public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override public void setUsernameError() {
        mEmailField.setError(getString(R.string.error));
    }

    @Override public void setPasswordError() {
        mPasswordField.setError(getString(R.string.error));
    }

    @Override public void navigateToHome() {
        try {
            if (mAuth.getCurrentUser().isEmailVerified()) {
                //TODO: Fix double intent transition. (Loads intent twice)
                navHomeToggle = false;
                startActivity(new Intent(this, FragmentManager.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            } else {
                Toast.makeText(this, "Please Verify Your Email.", Toast.LENGTH_SHORT).show();
            }
        }catch(NullPointerException e){
            Log.e(TAG, "NavigateToHome: User doesn't exist.");
        }
    }

    @Override public void hideKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override public void showKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager keyboard = (InputMethodManager)
            getSystemService(Context.INPUT_METHOD_SERVICE);
            keyboard.showSoftInput(view, 0);
        }
    }

    @Override public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.email_create_account_button) {
            setAccountDialog();
            showKeyboard();
        } else if (i == R.id.email_sign_in_button) {
            if(!TextUtils.isEmpty(mEmailField.toString()) && !TextUtils.isEmpty(mPasswordField.toString())) {
                showProgress();
                hideKeyboard();
            }else{hideProgress();}
            signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if(i == R.id.reset_password){
            setResetDialog();
            showKeyboard();
        } else if(i == R.id.createSubmitButton){
            showProgress();
            hideKeyboard();
        }
    }
}