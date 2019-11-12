package com.example.feedct.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.feedct.JSONManager;
import com.example.feedct.R;
import com.example.feedct.adapters.SectionsPageAdapter;
import com.example.feedct.Session;
import com.example.feedct.fragments.MinhasFragment;
import com.example.feedct.fragments.NotificacoesFragment;
import com.example.feedct.fragments.TodasFragment;
import com.example.feedct.jsonpojos.User;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
            public void onClick(View v) {
                TextInputLayout usernameTextInputLayout = ((TextInputLayout)findViewById(R.id.username));
                EditText usernameEditText = usernameTextInputLayout.getEditText();
                EditText passwordEditText = ((TextInputLayout)findViewById(R.id.password)).getEditText();

                String username = usernameEditText.getText().toString() + usernameTextInputLayout.getSuffixText();
                String password = passwordEditText.getText().toString();

                User currentUser = null;
                for (User user : JSONManager.users) {
                    if (user.getEmail().equals(username) && user.getPassword().equals(password)) {
                        currentUser = user;
                        break;
                    }
                }

                if (currentUser == null) {
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
                else {
                    new Session(username);
                    v.getContext().startActivity(new Intent(v.getContext(), HomeActivity.class));
                }
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
