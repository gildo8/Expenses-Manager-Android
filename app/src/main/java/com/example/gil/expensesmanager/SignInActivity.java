package com.example.gil.expensesmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gil.expensesmanager.model.Model;
import com.example.gil.expensesmanager.model.User;


/**
 * Created by gildo on 20/05/2016.
 */
public class SignInActivity extends AppCompatActivity {
    static final int NEW_USER_REQUEST = 1;
    ProgressBar progressBar;
    TextView waitLbl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_layout);

        //Check if there is user logged in then start MainActivity
        TextView registerLink = (TextView) findViewById(R.id.registerLink);
        final EditText userEmail = (EditText) findViewById(R.id.userEmail);
        final EditText userPassword = (EditText) findViewById(R.id.userPassword);
        Button logInButton = (Button) findViewById(R.id.signin_button);
        ImageView imageStart = (ImageView) findViewById(R.id.logo);
        TextView signInLbl = (TextView) findViewById(R.id.signIn);
        Button cancelBtn = (Button) findViewById(R.id.cancel_button);
        progressBar = (ProgressBar) findViewById(R.id.startProgressBar);
        waitLbl = (TextView) findViewById(R.id.wait_lbl);

        registerLink.setVisibility(View.INVISIBLE);
        userEmail.setVisibility(View.INVISIBLE);
        userPassword.setVisibility(View.INVISIBLE);
        logInButton.setVisibility(View.INVISIBLE);
        imageStart.setVisibility(View.INVISIBLE);
        signInLbl.setVisibility(View.INVISIBLE);
        cancelBtn.setVisibility(View.INVISIBLE);


        progressBar.setVisibility(View.VISIBLE);

        String check = Model.instance().CheckLoggedIn();
        if (check != null) {
            Model.instance().GetSingleUser(check, new Model.GetSingleUserListener() {
                @Override
                public void onResult(User result) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("NAME", result.getName());
                    intent.putExtra("UID", result.getId());
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onCancel() {

                }
            });
        } else {
            registerLink.setVisibility(View.VISIBLE);
            userEmail.setVisibility(View.VISIBLE);
            userPassword.setVisibility(View.VISIBLE);
            logInButton.setVisibility(View.VISIBLE);
            imageStart.setVisibility(View.VISIBLE);
            signInLbl.setVisibility(View.VISIBLE);
            cancelBtn.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            waitLbl.setVisibility(View.INVISIBLE);

            logInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String email = userEmail.getText().toString();
                    String pass = userPassword.getText().toString();

                    Model.instance().LogInUser(email, pass, new Model.FireBaseStringListener() {
                        @Override
                        public void onResult(String result) {
                            Model.instance().GetSingleUser(result, new Model.GetSingleUserListener() {
                                @Override
                                public void onResult(User result) {
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    intent.putExtra("NAME", result.getName());
                                    intent.putExtra("UID", result.getId());
                                    startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void onCancel() {

                                }
                            });
                        }

                        @Override
                        public void onCancel() {
                            Toast.makeText(getApplicationContext(),R.string.register_error,Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });

            registerLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                    startActivityForResult(intent, NEW_USER_REQUEST);
                }
            });

            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == NEW_USER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Intent intent = new Intent(getApplicationContext(),SignInActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("SignInActivity ", "- onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("SignInActivity ", "- onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("SignInActivity ", "- onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("SignInActivity ", "- onStop()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("SignInActivity ", "- onDestroy()");
    }

}
