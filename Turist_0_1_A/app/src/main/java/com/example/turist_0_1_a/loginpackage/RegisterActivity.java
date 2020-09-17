package com.example.turist_0_1_a.loginpackage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.turist_0_1_a.R;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private static Button back;
    private static Button register;


    private static EditText getUsername;
    private static EditText getPassword;
    private static EditText getConfirmedPassword;

    private RegisterController insertUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(registerIntent);
            }
        });

        getUsername = (EditText) findViewById(R.id.username);
        getPassword = (EditText) findViewById(R.id.password);
        getConfirmedPassword = (EditText) findViewById(R.id.confirmPassword);

        insertUser = new RegisterController();

        register = (Button) findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = getUsername.getText().toString();
                String pass = getPassword.getText().toString();
                String conf = getConfirmedPassword.getText().toString();

                if(user.isEmpty() || pass.isEmpty() || conf.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Add Credentials", Toast.LENGTH_SHORT).show();
                }else if(pass.equals(conf)==true){
                    insertUser.set(user, pass);
                    insertUser.start();

                    try {
                        insertUser.join();
                    } catch (InterruptedException e) {
                        System.out.println("Eroare la join --- login check wait thread dead ---");
                        e.printStackTrace();
                    }


                    if (insertUser.status == true) {
                        Intent registerIntent = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(registerIntent);
                    } else {

                        Toast.makeText(RegisterActivity.this, "Try again", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }
}
