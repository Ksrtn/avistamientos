package co.edu.unbosque.avistamientos.dominio;

import java.util.List;

public class AvistamientoInvalidoException extends RuntimeException {

    private final List<String> errores;

    public AvistamientoInvalidoException(List<String> errores) {
        super(String.join("; ", errores));
        this.errores = List.copyOf(errores);
    }

    public List<String> getErrores() {
        return errores;
    }
}