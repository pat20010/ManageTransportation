package com.project_develop_team.managetransportation;


import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project_develop_team.managetransportation.models.Users;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.usernameEditText)
    EditText usernameEditText;
    @BindView(R.id.passwordEditText)
    EditText passwordEditText;

    private DatabaseReference database;
    private FirebaseAuth auth;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        database = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();

    }

    private void signIn() {

        String username = usernameEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();

        if (validateForm(username, password)) {
            showProgressDialog();

            auth.signInWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    hideProgressDialog();
                    if (task.isSuccessful()) {
                        onAuthSuccess(task.getResult().getUser());
                    } else {
                        Toast.makeText(LoginActivity.this, R.string.user_login_fail, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (auth.getCurrentUser() != null) {
            onAuthSuccess(auth.getCurrentUser());
        }
    }

    @OnClick(R.id.loginButton)
    public void login() {
        signIn();
    }

    private void onAuthSuccess(FirebaseUser firebaseUser) {
        String email = firebaseUser.getEmail();
        String username = email;
        if (email != null && email.contains("@")) {
            username = email.split("@")[0];
        }

        Users user = new Users(username, email);
        database.child("users").child(firebaseUser.getUid()).setValue(user);

        startActivity(new Intent(this, MainTabActivity.class));
        finish();

    }

    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading. . .");
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private boolean validateForm(String username, String password) {
        if (TextUtils.isEmpty(username)) {
            usernameEditText.setError(getString(R.string.enter_username));
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError(getString(R.string.enter_password));
            return false;
        } else {
            usernameEditText.setError(null);
            passwordEditText.setError(null);
        }
        return true;
    }
}
