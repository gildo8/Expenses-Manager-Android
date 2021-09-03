package com.example.gil.expensesmanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gil.expensesmanager.model.Model;
import com.example.gil.expensesmanager.model.ModelSql;
import com.example.gil.expensesmanager.model.User;
import com.example.gil.expensesmanager.model.UserSql;

import java.util.List;

/**
 * Created by gildo on 20/05/2016.
 */
public class RegisterActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        final EditText userEmail = (EditText) findViewById(R.id.userEmail);
        final EditText userPassword = (EditText) findViewById(R.id.userPassword);
        final EditText userName = (EditText) findViewById(R.id.userName);
        TextView signInLink = (TextView) findViewById(R.id.signInLink);
        Button registerButton = (Button) findViewById(R.id.register_button);
        Button cancelBtn = (Button) findViewById(R.id.cancel_button);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = userEmail.getText().toString();
                String pass = userPassword.getText().toString();
                String name = userName.getText().toString();

                Model.instance().RegisterUser(email,pass,name,new Model.GetSingleUserListener() {
                    @Override
                    public void onResult(User result) { //User Registerd Succecfuly
                        Intent intent = new Intent();
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(getApplicationContext(),R.string.register_error,Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        signInLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("RegisterActivity ", "- onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("RegisterActivity ", "- onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("RegisterActivity ", "- onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("RegisterActivity ", "- onStop()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("RegisterActivity ", "- onDestroy()");
    }
}
