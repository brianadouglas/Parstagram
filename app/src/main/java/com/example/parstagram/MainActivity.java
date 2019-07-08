package com.example.parstagram;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    public ParseApplication app;
    EditText user; // username entered
    EditText pass; // password entered
    EditText email; // email address entered - only relevant for sign up

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialise tne Parse application
        app = new ParseApplication();
    }

    // on click for the log in button
    public void login (View v) {
        user = (EditText) findViewById(R.id.username);
        pass = (EditText) findViewById(R.id.password);

        app.logIn(user.getText().toString(), pass.getText().toString());
    }

    // on click for the sign up button
    public void signup (View v) {
        user = (EditText) findViewById(R.id.username);
        pass = (EditText) findViewById(R.id.password);
        email = (EditText) findViewById(R.id.email);

        app.signUp(user.getText().toString(), pass.getText().toString(), email.getText().toString());
    }
}
