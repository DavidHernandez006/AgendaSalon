package com.example.agendasalon;

import java.util.ArrayList;

/**
 * Autor: David - Momento 3
 *
 * Repositorio en memoria: administra el ArrayList de citas (CRUD básico + totales).
 * Es un Singleton: existe una sola instancia, así la lista no se pierde al rotar la pantalla.
 * Nota: los datos viven en memoria, por lo que se borran al cerrar la app por completo.
 */
public class CitaRepositorio {

    private static CitaRepositorio instancia;
    private final ArrayList<Cita> citas = new ArrayList<>();

    private CitaRepositorio() {
    }

    public static CitaRepositorio getInstancia() {
        if (instancia == null) {
            instancia = new CitaRepositorio();
        }
        return instancia;
    }

    /** True si ya hay una cita en esa fecha y hora (evita cruces de horario). */
    public boolean existeHorario(String fecha, String hora) {
        for (Cita c : citas) {
            if (c.getFecha().equals(fecha) && c.getHora().equals(hora)) {
                return true;
            }
        }
        return false;
    }

    /** CREATE: agrega la cita. Devuelve false si el horario está ocupado. */
    public boolean agregar(Cita cita) {
        if (existeHorario(cita.getFecha(), cita.getHora())) {
            return false;
        }
        citas.add(cita);
        return true;
    }

    /** READ: devuelve una copia de la lista para no exponer la colección interna. */
    public ArrayList<Cita> listar() {
        return new ArrayList<>(citas);
    }

    /** DELETE: elimina la última cita registrada. */
    public boolean eliminarUltima() {
        if (citas.isEmpty()) {
            return false;
        }
        citas.remove(citas.size() - 1);
        return true;
    }

    // ---------- Totales del negocio ----------
    public int cantidad() {
        return citas.size();
    }

    public double totalRecaudado() {
        double total = 0;
        for (Cita c : citas) {
            total += c.calcularPrecio(); // polimorfismo: cada subclase calcula a su manera
        }
        return total;
    }

    public double promedio() {
        return citas.isEmpty() ? 0 : totalRecaudado() / citas.size();
    }
}
