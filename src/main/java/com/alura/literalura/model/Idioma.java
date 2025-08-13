package com.alura.literalura.model;

public enum Idioma {
    PT("Português"),
    EN("Inglês"),
    ES("Espanhol"),
    FR("Francês"),
    OUTRO("Outro idioma");

    private String descricao;

    Idioma(String descricao) {
        this.descricao = descricao;
    }

    public static Idioma fromString(String texto) {
        try {
            return Idioma.valueOf(texto.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Idioma.OUTRO;
        }
    }

    public String getDescricao() {
        return descricao;
    }
}