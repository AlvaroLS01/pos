package com.comerzzia.dinosol.pos.persistence.encuestas.preguntas;

public class PreguntaEncuesta extends PreguntaEncuestaKey {
    private String texto;

    private String tipo;

    private String opciones;

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto == null ? null : texto.trim();
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo == null ? null : tipo.trim();
    }

    public String getOpciones() {
        return opciones;
    }

    public void setOpciones(String opciones) {
        this.opciones = opciones == null ? null : opciones.trim();
    }
}