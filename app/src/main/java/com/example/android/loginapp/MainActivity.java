package com.example.android.loginapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static String email,password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       Button adButton = (Button)findViewById(R.id.btn_login);
      final EditText txt_email = (EditText)findViewById(R.id.txt_email);
      final EditText txt_number = (EditText)findViewById(R.id.txt_number);

        adButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                email = txt_email.getText().toString();
                password = txt_number.getText().toString();

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(MainActivity.this,"Please Enter Email",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(MainActivity.this,"Please Enter Mobile Number",Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent numbersIntent = new Intent(MainActivity.this, qrscan.class);
                startActivity(numbersIntent);
            }
        });

    }
}
