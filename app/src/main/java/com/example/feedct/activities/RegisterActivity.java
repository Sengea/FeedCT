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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.feedct.DataManager;
import com.example.feedct.R;
import com.example.feedct.pojos.Curso;
import com.example.feedct.pojos.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.LinkedList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {
    private boolean doRegistry;

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

        doRegistry = true;
        Button buttonRegistar = findViewById(R.id.registar);
        buttonRegistar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final String username = usernameEditText.getText().toString();
                final String email = username + usernameTextInputLayout.getSuffixText();
                final String password = passwordEditText.getText().toString();
                final String passwordConfirm = passwordConfirmEditText.getText().toString();
                final String name = nameEditText.getText().toString();
                final String number = numberEditText.getText().toString();
                final String curso = cursoEditText.getText().toString();

                if (TextUtils.isEmpty(username)) {
                    doRegistry = false;
                    usernameTextInputLayout.setErrorEnabled(true);
                    usernameTextInputLayout.setError("Campo obrigatório");
                }

                if (TextUtils.isEmpty(password)) {
                    doRegistry = false;
                    passwordTextInputLayout.setErrorEnabled(true);
                    passwordTextInputLayout.setError("Campo obrigatório");
                }
                if (TextUtils.isEmpty(passwordConfirm)) {
                    doRegistry = false;
                    passwordConfirmTextInputLayout.setErrorEnabled(true);
                    passwordConfirmTextInputLayout.setError("Campo obrigatório");
                } else if (!password.equals(passwordConfirm)) {
                    doRegistry = false;
                    passwordConfirmTextInputLayout.setErrorEnabled(true);
                    passwordConfirmTextInputLayout.setError("Passwords não são iguais");
                }

                if (TextUtils.isEmpty(name)) {
                    doRegistry = false;
                    nameTextInputLayout.setErrorEnabled(true);
                    nameTextInputLayout.setError("Campo obrigatório");
                }

                if (TextUtils.isEmpty(number)) {
                    doRegistry = false;
                    numberTextInputLayout.setErrorEnabled(true);
                    numberTextInputLayout.setError("Campo obrigatório");
                }
                else if (!TextUtils.isDigitsOnly(number)) {
                    doRegistry = false;
                    numberTextInputLayout.setErrorEnabled(true);
                    numberTextInputLayout.setError("Campo deve ser um número");
                }

                if (TextUtils.isEmpty(curso)) {
                    doRegistry = false;
                    cursoTextInputLayout.setErrorEnabled(true);
                    cursoTextInputLayout.setError("Campo obrigatório");
                }

                DataManager.db.collection(DataManager.USERS).whereEqualTo("email", email).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                        boolean emailIsTaken = false;
                        if (documentSnapshots.size() > 0) {
                            emailIsTaken = true;
                            usernameTextInputLayout.setErrorEnabled(true);
                            usernameTextInputLayout.setError("Email já está em uso");
                        }

                        if (doRegistry && !emailIsTaken) {
                            DataManager.db.collection(DataManager.USERS).add(new User(email, password, name, Integer.valueOf(number), curso));
                            Toast toast = Toast.makeText(v.getContext(), "Conta criada com sucesso.", Toast.LENGTH_SHORT);
                            toast.show();

                            v.getContext().startActivity(new Intent(v.getContext(), LoginActivity.class));
                        }
                    }
                });
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
        for (Curso curso : DataManager.cursos) {
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
