package com.example.agendasalon;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Autor: David - Momento 3
 *
 * CAPA DE NEGOCIO - Superclase abstracta.
 * Encapsula los datos comunes de cualquier cita de peluquería.
 * Cada tipo de servicio (subclase) calcula su precio de forma diferente.
 */
public abstract class Cita {

    // ---------- Constantes del negocio ----------
    public static final double COSTO_LAVADO = 8000;
    public static final double COSTO_PEINADO = 12000;
    public static final double DESCUENTO_FRECUENTE = 0.10; // 10 %

    // ---------- Atributos privados (encapsulamiento) ----------
    private String nombreCliente;
    private String cedula;
    private String telefono;
    private String fecha;
    private String hora;
    private boolean lavado;
    private boolean peinado;
    private boolean clienteFrecuente;

    /** Constructor: usa los setters para que TODA la validación quede en un solo lugar. */
    public Cita(String nombreCliente, String cedula, String telefono, String fecha, String hora,
                boolean lavado, boolean peinado, boolean clienteFrecuente) {
        setNombreCliente(nombreCliente);
        setCedula(cedula);
        setTelefono(telefono);
        setFecha(fecha);
        setHora(hora);
        this.lavado = lavado;
        this.peinado = peinado;
        this.clienteFrecuente = clienteFrecuente;
    }

    // ---------- Getters y Setters (con validaciones) ----------
    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        if (nombreCliente == null || nombreCliente.trim().length() < 3) {
            throw new IllegalArgumentException("El nombre debe tener al menos 3 caracteres");
        }
        this.nombreCliente = nombreCliente.trim();
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        if (cedula == null || !cedula.trim().matches("\\d{6,10}")) {
            throw new IllegalArgumentException("La cédula debe tener entre 6 y 10 dígitos");
        }
        this.cedula = cedula.trim();
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        if (telefono == null || !telefono.trim().matches("\\d{10}")) {
            throw new IllegalArgumentException("El teléfono debe tener 10 dígitos");
        }
        this.telefono = telefono.trim();
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        if (fecha == null || fecha.trim().isEmpty()) {
            throw new IllegalArgumentException("La fecha es obligatoria");
        }
        this.fecha = fecha.trim();
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        if (hora == null || hora.trim().isEmpty()) {
            throw new IllegalArgumentException("La hora es obligatoria");
        }
        this.hora = hora.trim();
    }

    public boolean isLavado() {
        return lavado;
    }

    public void setLavado(boolean lavado) {
        this.lavado = lavado;
    }

    public boolean isPeinado() {
        return peinado;
    }

    public void setPeinado(boolean peinado) {
        this.peinado = peinado;
    }

    public boolean isClienteFrecuente() {
        return clienteFrecuente;
    }

    public void setClienteFrecuente(boolean clienteFrecuente) {
        this.clienteFrecuente = clienteFrecuente;
    }

    // ---------- Lógica común ----------

    /** Suma los servicios adicionales marcados. Las subclases pueden sobreescribirlo. */
    protected double calcularExtras() {
        double total = 0;
        if (lavado) total += COSTO_LAVADO;
        if (peinado) total += COSTO_PEINADO;
        return total;
    }

    /** Aplica el 10 % de descuento si el cliente es frecuente. */
    protected double aplicarDescuentoFrecuente(double subtotal) {
        return clienteFrecuente ? subtotal * (1 - DESCUENTO_FRECUENTE) : subtotal;
    }

    // ---------- Métodos abstractos: cada subclase los sobreescribe (@Override) ----------

    /** Precio final de la cita. */
    public abstract double calcularPrecio();

    /** Nombre del servicio (Corte, Color, Tratamiento). */
    public abstract String getTipoServicio();

    /** Texto corto que explica cómo se compone el precio de ese servicio. */
    public abstract String getDetalle();

    /** Ficha con el resumen de la cita, lista para mostrar en pantalla. */
    public String generarResumen() {
        StringBuilder sb = new StringBuilder();
        sb.append("Cliente: ").append(nombreCliente).append("\n");
        sb.append("Cédula: ").append(cedula).append("\n");
        sb.append("Teléfono: ").append(telefono).append("\n");
        sb.append("Fecha: ").append(fecha).append("   Hora: ").append(hora).append("\n");
        sb.append("Servicio: ").append(getTipoServicio()).append("\n");
        sb.append("Detalle: ").append(getDetalle()).append("\n");
        sb.append("Adicionales: ").append(formatoMoneda(calcularExtras())).append("\n");
        if (clienteFrecuente) {
            sb.append("Descuento cliente frecuente: -10%\n");
        }
        sb.append("TOTAL A PAGAR: ").append(formatoMoneda(calcularPrecio()));
        return sb.toString();
    }

    /** Da formato de pesos colombianos sin decimales. Ej: 25000 -> $25.000 */
    public static String formatoMoneda(double valor) {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.forLanguageTag("es-CO"));
        nf.setMaximumFractionDigits(0);
        return "$" + nf.format(Math.round(valor));
    }
}
