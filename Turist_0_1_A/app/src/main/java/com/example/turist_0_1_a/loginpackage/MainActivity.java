package com.example.turist_0_1_a.loginpackage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.turist_0_1_a.R;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static Button login;
    private static Button register;
    private static EditText getUsername;
    private static EditText getPassword;
    private static TextView status;

    private LoginController checkUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        checkUser = new LoginController();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUsername = (EditText) findViewById(R.id.username);
                getPassword = (EditText) findViewById(R.id.password);

                String user = getUsername.getText().toString();
                String pass = getPassword.getText().toString();

                if(user.isEmpty() || pass.isEmpty()){
                    Toast.makeText(MainActivity.this, "Add Credentials", Toast.LENGTH_SHORT).show();
                }else {
                    checkUser.set(user, pass);
                    checkUser.start();

                    try {
                        checkUser.join();
                    } catch (InterruptedException e) {
                        System.out.println("Eroare la join --- login check wait thread dead ---");
                        e.printStackTrace();
                    }

                    if (checkUser.status == true) {
                        Intent contentIntent = new Intent(getApplicationContext(),TabControlActivity.class);
                        contentIntent.putExtra("username",user);
                        startActivity(contentIntent);
                    } else {
                        Toast.makeText(MainActivity.this, "Wrong Credentials", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        register = (Button) findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(registerIntent);
            }
        });

    }



}
