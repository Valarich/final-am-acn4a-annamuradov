package com.example.myapplication;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private final ArrayList<Habit> habitos = new ArrayList<>();

    private LinearLayout listaHabitos;
    private TextView txtTotalHabitos;
    private TextView txtListaVacia;
    private Button btnLimpiarHabitos;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private ListenerRegistration listenerHabitos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (view, windowInsets) -> {
                    Insets systemBars = windowInsets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    int pantallaPadding = getResources()
                            .getDimensionPixelSize(R.dimen.pantalla_padding);

                    view.setPadding(
                            systemBars.left + pantallaPadding,
                            systemBars.top + pantallaPadding,
                            systemBars.right + pantallaPadding,
                            systemBars.bottom + pantallaPadding
                    );

                    return windowInsets;
                }
        );

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        Button btnAgregarHabito =
                findViewById(R.id.btnAgregarHabito);

        btnLimpiarHabitos =
                findViewById(R.id.btnLimpiarHabitos);

        listaHabitos =
                findViewById(R.id.listaHabitos);

        txtTotalHabitos =
                findViewById(R.id.txtTotalHabitos);

        txtListaVacia =
                findViewById(R.id.txtListaVacia);

        btnAgregarHabito.setOnClickListener(view -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    AddHabitActivity.class
            );

            startActivity(intent);
        });

        btnLimpiarHabitos.setOnClickListener(
                view -> confirmarLimpiarHabitos()
        );

        mostrarHabitos();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser usuarioActual =
                firebaseAuth.getCurrentUser();

        if (usuarioActual == null) {
            abrirPantallaLogin();
            return;
        }

        escucharHabitos(usuarioActual.getUid());
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (listenerHabitos != null) {
            listenerHabitos.remove();
            listenerHabitos = null;
        }
    }

    private void escucharHabitos(String usuarioId) {
        if (listenerHabitos != null) {
            listenerHabitos.remove();
        }

        listenerHabitos = firestore
                .collection("habitos")
                .whereEqualTo("usuarioId", usuarioId)
                .addSnapshotListener((snapshot, exception) -> {
                    if (exception != null) {
                        Toast.makeText(
                                this,
                                "No se pudieron cargar los hábitos.",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    habitos.clear();

                    if (snapshot != null) {
                        habitos.addAll(
                                snapshot.toObjects(Habit.class)
                        );

                        ordenarHabitosPorFecha();
                    }

                    mostrarHabitos();
                });
    }

    private void ordenarHabitosPorFecha() {
        habitos.sort((primerHabito, segundoHabito) -> {
            Date primeraFecha =
                    primerHabito.getCreadoEn();

            Date segundaFecha =
                    segundoHabito.getCreadoEn();

            if (primeraFecha == null
                    && segundaFecha == null) {
                return 0;
            }

            if (primeraFecha == null) {
                return 1;
            }

            if (segundaFecha == null) {
                return -1;
            }

            return primeraFecha.compareTo(segundaFecha);
        });
    }

    private void mostrarHabitos() {
        listaHabitos.removeAllViews();

        txtTotalHabitos.setText(
                getString(
                        R.string.texto_total_habitos,
                        habitos.size()
                )
        );

        if (habitos.isEmpty()) {
            txtListaVacia.setVisibility(View.VISIBLE);
            return;
        }

        txtListaVacia.setVisibility(View.GONE);

        for (int posicion = 0;
             posicion < habitos.size();
             posicion++) {

            Habit habito = habitos.get(posicion);

            TextView vistaHabito =
                    crearVistaHabito(habito, posicion);

            listaHabitos.addView(vistaHabito);
        }
    }

    private TextView crearVistaHabito(
            Habit habito,
            int posicion
    ) {
        TextView vistaHabito = new TextView(this);

        vistaHabito.setText(
                getString(
                        R.string.formato_habito,
                        posicion + 1,
                        habito.getNombre()
                )
        );

        float tamanoTexto = getResources()
                .getDimension(R.dimen.habito_texto)
                / getResources()
                .getDisplayMetrics()
                .scaledDensity;

        vistaHabito.setTextSize(tamanoTexto);

        vistaHabito.setTextColor(
                ContextCompat.getColor(
                        this,
                        R.color.color_texto_principal
                )
        );

        int margenChico = getResources()
                .getDimensionPixelSize(R.dimen.margen_chico);

        vistaHabito.setPadding(
                margenChico,
                margenChico,
                margenChico,
                margenChico
        );

        vistaHabito.setGravity(Gravity.CENTER_VERTICAL);
        vistaHabito.setMinHeight(48);
        vistaHabito.setClickable(true);
        vistaHabito.setLongClickable(true);

        LinearLayout.LayoutParams parametros =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        vistaHabito.setLayoutParams(parametros);

        aplicarEstadoVisual(vistaHabito, habito);

        vistaHabito.setOnClickListener(
                view -> cambiarEstadoHabito(habito)
        );

        vistaHabito.setOnLongClickListener(view -> {
            confirmarEliminarHabito(habito);
            return true;
        });

        return vistaHabito;
    }

    private void aplicarEstadoVisual(
            TextView vistaHabito,
            Habit habito
    ) {
        if (habito.isCompletado()) {
            vistaHabito.setPaintFlags(
                    vistaHabito.getPaintFlags()
                            | Paint.STRIKE_THRU_TEXT_FLAG
            );

            vistaHabito.setAlpha(0.55f);
        } else {
            vistaHabito.setPaintFlags(
                    vistaHabito.getPaintFlags()
                            & ~Paint.STRIKE_THRU_TEXT_FLAG
            );

            vistaHabito.setAlpha(1.0f);
        }
    }

    private void cambiarEstadoHabito(Habit habito) {
        if (habito.getId() == null) {
            return;
        }

        boolean nuevoEstado =
                !habito.isCompletado();

        firestore
                .collection("habitos")
                .document(habito.getId())
                .update("completado", nuevoEstado)
                .addOnFailureListener(exception ->
                        Toast.makeText(
                                this,
                                "No se pudo actualizar el hábito.",
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }

    private void confirmarEliminarHabito(Habit habito) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar hábito")
                .setMessage(
                        "¿Querés eliminar \""
                                + habito.getNombre()
                                + "\"?"
                )
                .setNegativeButton(
                        "Cancelar",
                        null
                )
                .setPositiveButton(
                        "Eliminar",
                        (dialog, which) ->
                                eliminarHabito(habito)
                )
                .show();
    }

    private void eliminarHabito(Habit habito) {
        if (habito.getId() == null) {
            return;
        }

        firestore
                .collection("habitos")
                .document(habito.getId())
                .delete()
                .addOnFailureListener(exception ->
                        Toast.makeText(
                                this,
                                "No se pudo eliminar el hábito.",
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }

    private void confirmarLimpiarHabitos() {
        if (habitos.isEmpty()) {
            Toast.makeText(
                    this,
                    "No hay hábitos para eliminar.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Eliminar todos los hábitos")
                .setMessage(
                        "Esta acción eliminará todos tus hábitos. "
                                + "No se puede deshacer."
                )
                .setNegativeButton(
                        "Cancelar",
                        null
                )
                .setPositiveButton(
                        "Eliminar todos",
                        (dialog, which) ->
                                limpiarHabitos()
                )
                .show();
    }

    private void limpiarHabitos() {
        FirebaseUser usuarioActual =
                firebaseAuth.getCurrentUser();

        if (usuarioActual == null) {
            abrirPantallaLogin();
            return;
        }

        btnLimpiarHabitos.setEnabled(false);

        firestore
                .collection("habitos")
                .whereEqualTo(
                        "usuarioId",
                        usuarioActual.getUid()
                )
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.isEmpty()) {
                        btnLimpiarHabitos.setEnabled(true);
                        return;
                    }

                    WriteBatch lote = firestore.batch();

                    for (DocumentSnapshot documento
                            : snapshot.getDocuments()) {

                        lote.delete(documento.getReference());
                    }

                    lote.commit()
                            .addOnSuccessListener(unused -> {
                                btnLimpiarHabitos.setEnabled(true);

                                Toast.makeText(
                                        this,
                                        "Todos los hábitos fueron eliminados.",
                                        Toast.LENGTH_SHORT
                                ).show();
                            })
                            .addOnFailureListener(exception -> {
                                btnLimpiarHabitos.setEnabled(true);

                                Toast.makeText(
                                        this,
                                        "No se pudieron eliminar los hábitos.",
                                        Toast.LENGTH_SHORT
                                ).show();
                            });
                })
                .addOnFailureListener(exception -> {
                    btnLimpiarHabitos.setEnabled(true);

                    Toast.makeText(
                            this,
                            "No se pudieron obtener los hábitos.",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    private void abrirPantallaLogin() {
        Intent intent = new Intent(
                MainActivity.this,
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