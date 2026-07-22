package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddHabitActivity extends AppCompatActivity {

    public static final String EXTRA_HABITO_CREADO =
            "extra_habito_creado";

    private EditText inputHabito;
    private Button btnGuardarHabito;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_habit);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (view, windowInsets) -> {
                    Insets systemBars = windowInsets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    view.setPadding(
                            systemBars.left + 24,
                            systemBars.top + 24,
                            systemBars.right + 24,
                            systemBars.bottom + 24
                    );

                    return windowInsets;
                }
        );

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        inputHabito = findViewById(R.id.inputHabito);
        btnGuardarHabito = findViewById(R.id.btnGuardarHabito);

        btnGuardarHabito.setOnClickListener(
                view -> guardarHabito()
        );
    }

    private void guardarHabito() {
        String textoHabito = inputHabito
                .getText()
                .toString()
                .trim();

        if (textoHabito.isEmpty()) {
            inputHabito.setError(
                    getString(R.string.mensaje_habito_vacio)
            );

            inputHabito.requestFocus();
            return;
        }

        FirebaseUser usuarioActual =
                firebaseAuth.getCurrentUser();

        if (usuarioActual == null) {
            abrirPantallaLogin();
            return;
        }

        btnGuardarHabito.setEnabled(false);

        Habit nuevoHabito = new Habit(
                textoHabito,
                usuarioActual.getUid()
        );

        firestore
                .collection("habitos")
                .add(nuevoHabito)
                .addOnSuccessListener(documentReference -> {
                    Intent resultado = new Intent();

                    resultado.putExtra(
                            EXTRA_HABITO_CREADO,
                            textoHabito
                    );

                    setResult(RESULT_OK, resultado);
                    finish();
                })
                .addOnFailureListener(exception -> {
                    btnGuardarHabito.setEnabled(true);

                    Toast.makeText(
                            this,
                            "No se pudo guardar el hábito.",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    private void abrirPantallaLogin() {
        Intent intent = new Intent(
                AddHabitActivity.this,
                LoginActivity.class
        );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }
}