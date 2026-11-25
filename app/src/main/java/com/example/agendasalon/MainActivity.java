package com.example.agendasalon;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

// David - Momento 3
public class MainActivity extends AppCompatActivity {

    // Vistas : variables para xml
    private EditText etNombre, etCedula, etTelefono, etFecha, etHora;
    private RadioGroup rgServicio;
    private CheckBox cbLavado, cbPeinado, cbFrecuente;
    private Button btnGuardar, btnLimpiar, btnCancelarUltima;
    private TextView tvResumen, tvTotales, tvLista;

    // Capa de negocio :acceso a las citas, guardarlas, listar y consular
    private final CitaRepositorio repositorio = CitaRepositorio.getInstancia();

    // Ciclo de vida: onCreate se ejecuta al abrir la pantalla (y al rotarla).
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_main);

        ajustarMargenesDelSistema();
        enlazarVistas();
        configurarEventos();
        actualizarAgenda();
    }

    // ajuste de margenes
    private void ajustarMargenesDelSistema() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.raiz), (v, insets) -> {
            Insets b = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(b.left, b.top, b.right, b.bottom);
            return insets;
        });
    }

    // busqueda de variables
    private void enlazarVistas() {
        etNombre = findViewById(R.id.etNombre);
        etCedula = findViewById(R.id.etCedula);
        etTelefono = findViewById(R.id.etTelefono);
        etFecha = findViewById(R.id.etFecha);
        etHora = findViewById(R.id.etHora);
        rgServicio = findViewById(R.id.rgServicio);
        cbLavado = findViewById(R.id.cbLavado);
        cbPeinado = findViewById(R.id.cbPeinado);
        cbFrecuente = findViewById(R.id.cbFrecuente);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnLimpiar = findViewById(R.id.btnLimpiar);
        btnCancelarUltima = findViewById(R.id.btnCancelarUltima);
        tvResumen = findViewById(R.id.tvResumen);
        tvTotales = findViewById(R.id.tvTotales);
        tvLista = findViewById(R.id.tvLista);
    }

    // Eventos de clic (listeners)
    private void configurarEventos() {
        etFecha.setOnClickListener(v -> mostrarSelectorFecha());
        etHora.setOnClickListener(v -> mostrarSelectorHora());
        btnGuardar.setOnClickListener(v -> procesarCita());
        btnLimpiar.setOnClickListener(v -> {
            limpiarCampos();
            tvResumen.setText(R.string.resumen_vacio);
            Toast.makeText(this, R.string.msg_campos_limpios, Toast.LENGTH_SHORT).show();
        });
        btnCancelarUltima.setOnClickListener(v -> {
            if (repositorio.eliminarUltima()) {
                actualizarAgenda();
                Toast.makeText(this, R.string.msg_cita_cancelada, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //  Selectores de fecha y hora
    private void mostrarSelectorFecha() {
        Calendar hoy = Calendar.getInstance();
        DatePickerDialog dialogo = new DatePickerDialog(this,
                (vista, anio, mes, dia) ->
                        etFecha.setText(String.format(Locale.US, "%02d/%02d/%04d", dia, mes + 1, anio)),
                hoy.get(Calendar.YEAR), hoy.get(Calendar.MONTH), hoy.get(Calendar.DAY_OF_MONTH));
        dialogo.getDatePicker().setMinDate(System.currentTimeMillis() - 1000); // no permite fechas pasadas
        dialogo.show();
    }

    private void mostrarSelectorHora() {
        new TimePickerDialog(this,
                (vista, hora, minuto) -> etHora.setText(String.format(Locale.US, "%02d:%02d", hora, minuto)),
                9, 0, true).show();
    }

    // Acción principal: validar, calcular y guardar ----------
    private void procesarCita() {
        String nombre = etNombre.getText().toString().trim();
        String cedula = etCedula.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String fecha = etFecha.getText().toString().trim();
        String hora = etHora.getText().toString().trim();

        // 1) Validación de campos vacíos ANTES de crear cualquier objeto
        if (nombre.isEmpty() || cedula.isEmpty() || telefono.isEmpty()
                || fecha.isEmpty() || hora.isEmpty()) {
            Toast.makeText(this, R.string.msg_campos_vacios, Toast.LENGTH_SHORT).show();
            return;
        }

        int servicioId = rgServicio.getCheckedRadioButtonId();
        if (servicioId == -1) {
            Toast.makeText(this, R.string.msg_sin_servicio, Toast.LENGTH_SHORT).show();
            return;
        }

        boolean lavado = cbLavado.isChecked();
        boolean peinado = cbPeinado.isChecked();
        boolean frecuente = cbFrecuente.isChecked();

        try {
            // 2) Polimorfismo: una variable de la superclase guarda cualquier subclase
            Cita cita;
            if (servicioId == R.id.rbCorte) {
                cita = new CitaCorte(nombre, cedula, telefono, fecha, hora, lavado, peinado, frecuente);
            } else if (servicioId == R.id.rbColor) {
                cita = new CitaColor(nombre, cedula, telefono, fecha, hora, lavado, peinado, frecuente);
            } else {
                cita = new CitaTratamiento(nombre, cedula, telefono, fecha, hora, lavado, peinado, frecuente);
            }

            // 3) Guardar en el repositorio (rechaza horarios repetidos)
            if (!repositorio.agregar(cita)) {
                Toast.makeText(this, R.string.msg_horario_ocupado, Toast.LENGTH_LONG).show();
                return;
            }

            // 4) Mostrar resultados
            tvResumen.setText(cita.generarResumen());
            actualizarAgenda();
            limpiarCampos();
            Toast.makeText(this, R.string.msg_cita_guardada, Toast.LENGTH_SHORT).show();

        } catch (IllegalArgumentException e) {
            // Los setters de Cita lanzan este error si un dato no cumple las reglas
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // Refresca la lista de citas y los totales a partir del repositorio.
    private void actualizarAgenda() {
        ArrayList<Cita> citas = repositorio.listar();

        tvTotales.setText("Citas registradas: " + repositorio.cantidad()
                + "\nTotal recaudado: " + Cita.formatoMoneda(repositorio.totalRecaudado())
                + "\nPromedio por cita: " + Cita.formatoMoneda(repositorio.promedio()));

        if (citas.isEmpty()) {
            tvLista.setText(R.string.agenda_vacia);
            btnCancelarUltima.setEnabled(false);
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < citas.size(); i++) {
            Cita c = citas.get(i);
            sb.append(i + 1).append(". ").append(c.getFecha()).append(" ").append(c.getHora())
                    .append(" - ").append(c.getNombreCliente())
                    .append(" (").append(c.getTipoServicio()).append(") - ")
                    .append(Cita.formatoMoneda(c.calcularPrecio()));
            if (i < citas.size() - 1) sb.append("\n");
        }
        tvLista.setText(sb.toString());
        btnCancelarUltima.setEnabled(true);
    }

    // Deja el formulario listo para registrar una nueva cita.
    private void limpiarCampos() {
        etNombre.setText("");
        etCedula.setText("");
        etTelefono.setText("");
        etFecha.setText("");
        etHora.setText("");
        rgServicio.clearCheck();
        cbLavado.setChecked(false);
        cbPeinado.setChecked(false);
        cbFrecuente.setChecked(false);
        etNombre.requestFocus();
    }
}
