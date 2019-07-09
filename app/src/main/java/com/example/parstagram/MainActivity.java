package com.example.parstagram;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class MainActivity extends AppCompatActivity implements TextWatcher {

    private final String TAG = "Main";
    EditText username; // username entered
    EditText password; // password entered
    EditText email; // email address entered - only relevant for sign up
    Button loginBtn;
    Button signUpBtn;
    public String persistUsername;
    public String persistPassword;
    boolean persisted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        email = (EditText) findViewById(R.id.email);

        // setting up the text change listeners
        username.addTextChangedListener(this);
        password.addTextChangedListener(this);
        email.addTextChangedListener(this);

        loginBtn = (Button) findViewById(R.id.logIn);
        signUpBtn = (Button) findViewById(R.id.create);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        persistUsername = pref.getString("username", "n/a");
        persistPassword = pref.getString("password", "n/a");

        if (persistUsername != "n/a" && persistPassword != "n/a") {
            persisted = true; // signifies that a username and password has been found from a previous session
            login(findViewById(R.id.loginPage));
        }

    }

    // on click for the log in button
    public void login (View v) {

        // in the case that a login has not persisted, pull from the EditText input
        if (!persisted) {
            persistUsername = username.getText().toString();
            persistPassword = password.getText().toString();
        }

        ParseUser.logInInBackground(persistUsername, persistPassword, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    // Successful login
                    Toast.makeText(MainActivity.this, "Successful login", Toast.LENGTH_SHORT).show();
                } else {
                    // Failure
                    Log.e(TAG, "Failed log in", e);
                }
            }
        });
    }

    // on click for the sign up button
    public void signUp (View v) {
        // Create the ParseUser
        ParseUser user = new ParseUser();
        // Set core properties
        user.setUsername(username.getText().toString());
        user.setPassword(password.getText().toString());
        user.setEmail(email.getText().toString());
        // Implement sign up in the background
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    // Successful login
                    // TODO: continue onto the relevant activity
                    Toast.makeText(MainActivity.this, "Successful sign up", Toast.LENGTH_SHORT).show();
                } else {
                    // Unsuccessful - could remain on the main activity
                    Log.e(TAG, "Failed sign up", e);
                }
            }
        });
    }

    public void logOut() {
        ParseUser.logOut();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        // enables the buttons based on which fields are filled in
        if (username.getText().length() > 0 && password.getText().length() > 0) {
            loginBtn.setEnabled(true);
            if (email.getText().length() > 0) {
                signUpBtn.setEnabled(true);
            }
        }
    }
}
