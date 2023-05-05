package com.cursoandroid.whatsapp.model;

public class Conversa {
    private String idRemetente;
    private String idDestinatario;
    private String ultimaMensagem;
    private boolean enfase = false;
    private Usuario usuarioExibicao;
    private boolean check = false;
    private Grupo grupo;
    private boolean grupoConversa = false;

    public Conversa(){}

    public Conversa(String idRemetente, String idDestinatario, String ultimaMensagem, boolean enfase, Usuario usuarioExibicao,
                    boolean check, Grupo grupo, boolean grupoConversa) {
        this.idRemetente = idRemetente;
        this.idDestinatario = idDestinatario;
        this.ultimaMensagem = ultimaMensagem;
        this.enfase = enfase;
        this.usuarioExibicao = usuarioExibicao;
        this.check = check;
        this.grupo = grupo;
        this.grupoConversa = grupoConversa;
    }

    public String getIdRemetente() {
        return idRemetente;
    }

    public void setIdRemetente(String idRemetente) {
        this.idRemetente = idRemetente;
    }

    public String getIdDestinatario() {
        return idDestinatario;
    }

    public void setIdDestinatario(String idDestinatario) {
        this.idDestinatario = idDestinatario;
    }

    public String getUltimaMensagem() {
        return ultimaMensagem;
    }

    public void setUltimaMensagem(String ultimaMensagem) {
        this.ultimaMensagem = ultimaMensagem;
    }

    public Usuario getUsuarioExibicao() {
        return usuarioExibicao;
    }

    public void setUsuarioExibicao(Usuario usuarioExibicao) {
        this.usuarioExibicao = usuarioExibicao;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public boolean isGrupoConversa() {
        return grupoConversa;
    }

    public void setGrupoConversa(boolean grupoConversa){
        this.grupoConversa = grupoConversa;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    public boolean isEnfase() {
        return enfase;
    }

    public void setEnfase(boolean enfase) {
        this.enfase = enfase;
    }
}
