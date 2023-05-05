package com.cursoandroid.whatsapp.model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class Mensagem implements Serializable {
    private String id;
    private String idUsuario;
    private String nome = "";
    private String mensagem;
    private String imagem;
    private Mensagem quote;

    public Mensagem(){}

    public Mensagem(String idUsuario, String mensagem, String imagem) {
        this.idUsuario = idUsuario;
        this.mensagem = mensagem;
        this.imagem = imagem;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Mensagem getQuote() {
        return quote;
    }

    public void setQuote(Mensagem quote) {
        this.quote = quote;
    }
}
