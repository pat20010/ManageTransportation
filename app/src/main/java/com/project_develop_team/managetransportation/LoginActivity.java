package com.project_develop_team.managetransportation;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.username_edit_text)
    EditText usernameEditText;
    @BindView(R.id.password_edit_text)
    EditText passwordEditText;

    private FirebaseAuth auth;

    private ProgressDialog progressDialog;

    private static final String SHARED_PREFERENCES = "SharedPreferences";
    private static final String USER_NAME = "UserName";
    private static final String EMPTY = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        rememberUsername();
        auth = FirebaseAuth.getInstance();
    }

    @OnClick(R.id.login_button)
    public void login() {

        final String username = usernameEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();

        if (validateForm(username, password)) {
            showProgressDialog();

            auth.signInWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    hideProgressDialog();
                    if (task.isSuccessful()) {
                        onAuthSuccess();
                    } else {
                        alertDialog();
                    }
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (auth.getCurrentUser() != null) {
            onAuthSuccess();
        }
    }

    private void onAuthSuccess() {

        startActivity(new Intent(this, MainBottomNavigation.class));
        finish();
    }

    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.loading));
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
        }

        return true;
    }

    public void alertDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

        if (isNetworkAvailable()) {
            builder.setTitle(R.string.login_fail);
            builder.setMessage(R.string.internet_connect_fail);
        } else {
            builder.setTitle(R.string.login_incorrect);
            builder.setMessage(R.string.user_login_fail);
        }
        builder.setPositiveButton(android.R.string.ok, null);
        builder.show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activityNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activityNetworkInfo == null;
    }

    private void rememberUsername() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        usernameEditText.setText(sharedPreferences.getString(USER_NAME, EMPTY));
        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                editor.putString(USER_NAME, s.toString());
                editor.apply();
            }
        });
    }
}