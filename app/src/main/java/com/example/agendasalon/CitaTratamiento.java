package com.example.agendasalon;

/**
 * Autor: David - Momento 3
 * Subclase: cita de tratamiento capilar. El lavado ya está incluido, por eso
 * se sobreescribe calcularExtras() para NO cobrarlo dos veces.
 */
public class CitaTratamiento extends Cita {

    public static final double PRECIO_BASE = 55000;

    public CitaTratamiento(String nombreCliente, String cedula, String telefono, String fecha, String hora,
                           boolean lavado, boolean peinado, boolean clienteFrecuente) {
        super(nombreCliente, cedula, telefono, fecha, hora, lavado, peinado, clienteFrecuente);
    }

    /** Aquí solo se cobra el peinado; el lavado va incluido en el tratamiento. */
    @Override
    protected double calcularExtras() {
        return isPeinado() ? COSTO_PEINADO : 0;
    }

    @Override
    public double calcularPrecio() {
        double subtotal = PRECIO_BASE + calcularExtras();
        return aplicarDescuentoFrecuente(subtotal);
    }

    @Override
    public String getTipoServicio() {
        return "Tratamiento";
    }

    @Override
    public String getDetalle() {
        return "Base " + formatoMoneda(PRECIO_BASE) + " (lavado incluido sin costo)";
    }
}
