package com.example.parstagram;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

public class LoginActivity extends AppCompatActivity implements TextWatcher {

    private final String TAG = "Main";
    EditText username; // username entered
    EditText password; // password entered
    EditText email; // email address entered - only relevant for sign up
    Button loginBtn;
    Button signUpBtn;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;

        // checking if the past user logged in has persisted through Parse
        if (ParseUser.getCurrentUser() != null) {
            // there is a user logged in from a previous session
            // leads them to the Feed Activity
            Intent intent = new Intent(this, FeedActivity.class);
            this.startActivity(intent);
            return; // ensures that the remainder of this activity does not run
        }

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        email = (EditText) findViewById(R.id.email);

        // setting up the text change listeners
        username.addTextChangedListener(this);
        password.addTextChangedListener(this);
        email.addTextChangedListener(this);

        loginBtn = (Button) findViewById(R.id.logIn);
        signUpBtn = (Button) findViewById(R.id.create);


    }

    // on click for the log in button
    public void login (View v) {

        // in the case that a login has not persisted, pull from the EditText input

        ParseUser.logInInBackground(username.getText().toString(), password.getText().toString(), new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    // Successful login
                    Toast.makeText(LoginActivity.this, "Successful login", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, FeedActivity.class);
                    context.startActivity(intent);
                    finish();
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
                    Toast.makeText(LoginActivity.this, "Successful sign up", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, FeedActivity.class);
                    context.startActivity(intent);
                    finish();
                } else {
                    // Unsuccessful - could remain on the main activity
                    Log.e(TAG, "Failed sign up", e);
                }
            }
        });


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
