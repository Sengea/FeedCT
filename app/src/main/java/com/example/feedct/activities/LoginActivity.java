package com.example.feedct.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.feedct.DataManager;
import com.example.feedct.R;
import com.example.feedct.Session;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login");

        Button buttonLogin = findViewById(R.id.login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                TextInputLayout usernameTextInputLayout = ((TextInputLayout)findViewById(R.id.username));
                EditText usernameEditText = usernameTextInputLayout.getEditText();
                EditText passwordEditText = ((TextInputLayout)findViewById(R.id.password)).getEditText();

                final String username = usernameEditText.getText().toString() + usernameTextInputLayout.getSuffixText();
                final String password = passwordEditText.getText().toString();

                DataManager.db.collection("users").whereEqualTo("email", username).whereEqualTo("password", password).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                        if (documents.size() == 1) {
                            new Session(username);
                            v.getContext().startActivity(new Intent(v.getContext(), HomeActivity.class));
                        }
                        else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                            builder.setMessage("Login inválido");
                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ((TextInputLayout)findViewById(R.id.password)).getEditText().setText("");
                                }
                            });
                            builder.show();
                        }
                    }
                });
            }
        });

        Button buttonRegister = findViewById(R.id.registar);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.getContext().startActivity(new Intent(v.getContext(), RegisterActivity.class));
            }
        });
    }
}
