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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText inputNombre;
    private EditText inputEmailRegistro;
    private EditText inputPasswordRegistro;
    private EditText inputConfirmarPassword;
    private Button btnRegistrarse;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

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
        firestore = FirebaseFirestore.getInstance();

        inputNombre = findViewById(R.id.inputNombre);
        inputEmailRegistro = findViewById(R.id.inputEmailRegistro);
        inputPasswordRegistro = findViewById(R.id.inputPasswordRegistro);
        inputConfirmarPassword = findViewById(R.id.inputConfirmarPassword);

        btnRegistrarse = findViewById(R.id.btnRegistrarse);
        Button btnVolverLogin = findViewById(R.id.btnVolverLogin);

        btnRegistrarse.setOnClickListener(view -> registrarUsuario());

        btnVolverLogin.setOnClickListener(view -> finish());
    }

    private void registrarUsuario() {
        String nombre = inputNombre.getText().toString().trim();
        String email = inputEmailRegistro.getText().toString().trim();
        String password = inputPasswordRegistro.getText().toString();
        String confirmarPassword =
                inputConfirmarPassword.getText().toString();

        if (TextUtils.isEmpty(nombre)
                || TextUtils.isEmpty(email)
                || TextUtils.isEmpty(password)
                || TextUtils.isEmpty(confirmarPassword)) {

            Toast.makeText(
                    this,
                    R.string.mensaje_campos_obligatorios,
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (password.length() < 6) {
            Toast.makeText(
                    this,
                    R.string.mensaje_password_corto,
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (!password.equals(confirmarPassword)) {
            Toast.makeText(
                    this,
                    R.string.mensaje_passwords_no_coinciden,
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        btnRegistrarse.setEnabled(false);

        firebaseAuth
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (!task.isSuccessful()) {
                        btnRegistrarse.setEnabled(true);

                        Toast.makeText(
                                this,
                                R.string.mensaje_registro_error,
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    FirebaseUser usuario = firebaseAuth.getCurrentUser();

                    if (usuario == null) {
                        btnRegistrarse.setEnabled(true);

                        Toast.makeText(
                                this,
                                R.string.mensaje_registro_error,
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    guardarUsuarioEnFirestore(
                            usuario.getUid(),
                            nombre,
                            email
                    );
                });
    }

    private void guardarUsuarioEnFirestore(
            String usuarioId,
            String nombre,
            String email
    ) {
        Map<String, Object> datosUsuario = new HashMap<>();
        datosUsuario.put("nombre", nombre);
        datosUsuario.put("email", email);
        datosUsuario.put("creadoEn", FieldValue.serverTimestamp());

        firestore
                .collection("usuarios")
                .document(usuarioId)
                .set(datosUsuario)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(
                            this,
                            R.string.mensaje_registro_exitoso,
                            Toast.LENGTH_SHORT
                    ).show();

                    Intent intent = new Intent(
                            RegisterActivity.this,
                            MainActivity.class
                    );

                    intent.addFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    );

                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(exception -> {
                    btnRegistrarse.setEnabled(true);

                    Toast.makeText(
                            this,
                            R.string.mensaje_registro_error,
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }
}