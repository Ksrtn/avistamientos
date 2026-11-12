package co.edu.unbosque.avistamientos.validacion;

import java.time.LocalDate;

public class RelojFijo implements Reloj {

    private final LocalDate hoy;

    public RelojFijo(LocalDate hoy) {
        this.hoy = hoy;
    }

    @Override
    public LocalDate hoy() {
        return hoy;
    }
}