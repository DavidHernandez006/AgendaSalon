package com.example.agendasalon;

/**
 * Autor: David - Momento 3
 * Subclase: cita de color/tinte. Precio = base + insumos de tinte + adicionales (- descuento frecuente).
 */
public class CitaColor extends Cita {

    public static final double PRECIO_BASE = 60000;
    public static final double COSTO_INSUMOS = 20000; // tinte y productos

    public CitaColor(String nombreCliente, String cedula, String telefono, String fecha, String hora,
                     boolean lavado, boolean peinado, boolean clienteFrecuente) {
        super(nombreCliente, cedula, telefono, fecha, hora, lavado, peinado, clienteFrecuente);
    }

    @Override
    public double calcularPrecio() {
        double subtotal = PRECIO_BASE + COSTO_INSUMOS + calcularExtras();
        return aplicarDescuentoFrecuente(subtotal);
    }

    @Override
    public String getTipoServicio() {
        return "Color";
    }

    @Override
    public String getDetalle() {
        return "Base " + formatoMoneda(PRECIO_BASE) + " + insumos " + formatoMoneda(COSTO_INSUMOS);
    }
}
