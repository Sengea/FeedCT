package com.example.feedct.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabWidget;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.feedct.JSONManager;
import com.example.feedct.R;
import com.example.feedct.jsonpojos.Curso;
import com.example.feedct.jsonpojos.User;
import com.google.android.material.textfield.TextInputLayout;

import org.w3c.dom.Text;

import java.util.LinkedList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);
        getSupportActionBar().setTitle("Registar");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final TextInputLayout usernameTextInputLayout = findViewById(R.id.username);
        final TextInputLayout passwordTextInputLayout = findViewById(R.id.password);
        final TextInputLayout passwordConfirmTextInputLayout = findViewById(R.id.confirmPassword);
        final TextInputLayout nameTextInputLayout = findViewById(R.id.name);
        final TextInputLayout numberTextInputLayout = findViewById(R.id.number);
        final TextInputLayout cursoTextInputLayout = findViewById(R.id.curso);

        final EditText usernameEditText = usernameTextInputLayout.getEditText();
        final EditText passwordEditText = passwordTextInputLayout.getEditText();
        final EditText passwordConfirmEditText = passwordConfirmTextInputLayout.getEditText();
        final EditText nameEditText = nameTextInputLayout.getEditText();
        final EditText numberEditText = numberTextInputLayout.getEditText();
        final EditText cursoEditText = cursoTextInputLayout.getEditText();

        cursoEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    showCursoDialog(v, cursoEditText);
            }
        });
       /* cursoEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCursoDialog(v, cursoEditText);
            }
        });*/

        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (usernameTextInputLayout.isErrorEnabled())
                    usernameTextInputLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (passwordTextInputLayout.isErrorEnabled())
                    passwordTextInputLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        passwordConfirmEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (passwordConfirmTextInputLayout.isErrorEnabled())
                    passwordConfirmTextInputLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (nameTextInputLayout.isErrorEnabled())
                    nameTextInputLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        numberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (numberTextInputLayout.isErrorEnabled())
                    numberTextInputLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Button buttonRegistar = findViewById(R.id.registar);
        buttonRegistar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String email = username + usernameTextInputLayout.getSuffixText();
                String password = passwordEditText.getText().toString();
                String passwordConfirm = passwordConfirmEditText.getText().toString();
                String name = nameEditText.getText().toString();
                String number = numberEditText.getText().toString();

                boolean doRegestry = true;

                if (TextUtils.isEmpty(username)) {
                    doRegestry = false;
                    usernameTextInputLayout.setErrorEnabled(true);
                    usernameTextInputLayout.setError("Campo obrigatório");

                } else {
                    boolean found = false;
                    for (User user : JSONManager.users) {
                        if (user.getEmail().equals(email)) {
                            found = true;
                            break;
                        }
                    }

                    if (found) {
                        doRegestry = false;
                        usernameTextInputLayout.setErrorEnabled(true);
                        usernameTextInputLayout.setError("Email já está em uso");
                    }
                }
                if (TextUtils.isEmpty(password)) {
                    doRegestry = false;
                    passwordTextInputLayout.setErrorEnabled(true);
                    passwordTextInputLayout.setError("Campo obrigatório");
                }
                if (TextUtils.isEmpty(passwordConfirm)) {
                    doRegestry = false;
                    passwordConfirmTextInputLayout.setErrorEnabled(true);
                    passwordConfirmTextInputLayout.setError("Campo obrigatório");
                } else if (!password.equals(passwordConfirm)) {
                    doRegestry = false;
                    passwordConfirmTextInputLayout.setErrorEnabled(true);
                    passwordConfirmTextInputLayout.setError("Passwords não são iguais");
                }

                if (TextUtils.isEmpty(name)) {
                    doRegestry = false;
                    nameTextInputLayout.setErrorEnabled(true);
                    nameTextInputLayout.setError("Campo obrigatório");

                }
                if (TextUtils.isEmpty(number)) {
                    doRegestry = false;
                    numberTextInputLayout.setErrorEnabled(true);
                    numberTextInputLayout.setError("Campo obrigatório");
                }
                else if (!TextUtils.isDigitsOnly(number)) {
                    doRegestry = false;
                    numberTextInputLayout.setErrorEnabled(true);
                    numberTextInputLayout.setError("Campo deve ser um número");
                }


                if (doRegestry) {
                    JSONManager.users.add(new User(email, password, name, Integer.valueOf(number), "teste"));
                    Toast toast = Toast.makeText(v.getContext(), "Conta criada com sucesso.", Toast.LENGTH_SHORT);
                    toast.show();

                    v.getContext().startActivity(new Intent(v.getContext(), LoginActivity.class));
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    private void showCursoDialog(View v, final EditText cursoEditText) {
        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setTitle("Curso");

        final List<String> cursosSiglas = new LinkedList<>();
        for (Curso curso : JSONManager.cursos) {
            cursosSiglas.add(curso.getSigla());
        }

        builder.setItems(cursosSiglas.toArray(new String[0]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cursoEditText.setText(cursosSiglas.get(which));
            }
        });

        builder.show();
    }
}
