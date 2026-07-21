package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail;
    private EditText inputPassword;
    private Button btnIniciarSesion;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (view, windowInsets) -> {
                    Insets systemBars = windowInsets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    view.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return windowInsets;
                }
        );

        firebaseAuth = FirebaseAuth.getInstance();

        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);

        Button btnIrRegistro = findViewById(R.id.btnIrRegistro);

        btnIniciarSesion.setOnClickListener(
                view -> iniciarSesion()
        );

        btnIrRegistro.setOnClickListener(view -> {
            Intent intent = new Intent(
                    LoginActivity.this,
                    RegisterActivity.class
            );

            startActivity(intent);
        });
    }

    private void iniciarSesion() {
        String email = inputEmail
                .getText()
                .toString()
                .trim();

        String password = inputPassword
                .getText()
                .toString();

        if (TextUtils.isEmpty(email)
                || TextUtils.isEmpty(password)) {

            Toast.makeText(
                    this,
                    R.string.mensaje_campos_obligatorios,
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        btnIniciarSesion.setEnabled(false);

        firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    btnIniciarSesion.setEnabled(true);

                    if (!task.isSuccessful()) {
                        Toast.makeText(
                                this,
                                R.string.mensaje_login_error,
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    Toast.makeText(
                            this,
                            R.string.mensaje_login_exitoso,
                            Toast.LENGTH_SHORT
                    ).show();

                    Intent intent = new Intent(
                            LoginActivity.this,
                            MainActivity.class
                    );

                    intent.addFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    );

                    startActivity(intent);
                    finish();
                });
    }
}