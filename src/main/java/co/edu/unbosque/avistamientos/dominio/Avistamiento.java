package co.edu.unbosque.avistamientos.dominio;

/**
 * Avistamiento de un ave. Es un objeto del dominio: no sabe nada de JPA, de Spring ni de HTTP.
 * La persistencia lo guarda con su propia clase (AvistamientoEntidad).
 */
public class Avistamiento {

    private Long id;
    private String especie;
    private String lugar;
    private String fecha;
    private String observador;

    public Avistamiento() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEspecie() { return especie; }
    public void setEspecie(String especie) { this.especie = especie; }
    public String getLugar() { return lugar; }
    public void setLugar(String lugar) { this.lugar = lugar; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public String getObservador() { return observador; }
    public void setObservador(String observador) { this.observador = observador; }
}
