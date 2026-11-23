package com.example.agendasalon;

/**
 * Autor: David - Momento 3
 * Subclase: cita de corte de cabello. Precio = base + adicionales (- descuento frecuente).
 */
public class CitaCorte extends Cita {

    public static final double PRECIO_BASE = 25000;

    public CitaCorte(String nombreCliente, String cedula, String telefono, String fecha, String hora,
                     boolean lavado, boolean peinado, boolean clienteFrecuente) {
        super(nombreCliente, cedula, telefono, fecha, hora, lavado, peinado, clienteFrecuente);
    }

    @Override
    public double calcularPrecio() {
        double subtotal = PRECIO_BASE + calcularExtras();
        return aplicarDescuentoFrecuente(subtotal);
    }

    @Override
    public String getTipoServicio() {
        return "Corte";
    }

    @Override
    public String getDetalle() {
        return "Corte de cabello (base " + formatoMoneda(PRECIO_BASE) + ")";
    }
}
