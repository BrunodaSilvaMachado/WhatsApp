package com.cursoandroid.whatsapp.serviceImpl;

import androidx.annotation.NonNull;
import com.cursoandroid.whatsapp.config.FirebaseConfig;
import com.cursoandroid.whatsapp.model.Mensagem;
import com.cursoandroid.whatsapp.service.Service;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;

public class MensagemService implements Service {
    private static final String TAG = MensagemService.class.getSimpleName();
    private static DatabaseReference mensagemRef = null;
    private DatabaseReference mensagemDir = null;
    private ChildEventListener eventListener = null;
    private boolean isPrepare = false;
    public MensagemService(){
        if (mensagemRef == null){
            mensagemRef = FirebaseConfig.getDatabaseReference().child("mensagens");
        }

    }

    @Override
    public void start() {
        if (!isPrepare){
            throw new RuntimeException(TAG + ": First prepare the service.");
        }
        if (mensagemDir != null) {
            mensagemDir.addChildEventListener(eventListener);
        }
    }

    @Override
    public void restart() {
        stop();
        start();
    }

    @Override
    public void stop() {
        if (mensagemDir != null) {
            mensagemDir.removeEventListener(eventListener);
        }
    }

    @Override
    public void finish() {
        mensagemRef = null;
        mensagemDir = null;
        isPrepare = false;
    }

    public void save(@NonNull String idRemetente, @NonNull String idDestinatario, Mensagem mensagem){
        mensagemRef.child(idRemetente).child(idDestinatario).push().setValue(mensagem);
    }

    public void delete(@NonNull String idRemetente, @NonNull String idDestinatario, @NonNull Mensagem mensagem){
        mensagemRef.child(idRemetente).child(idDestinatario).child(mensagem.getId()).removeValue();
    }

    public void setEventListener(ChildEventListener eventListener){
        this.eventListener = eventListener;
    }

    public void prepare(String idRemetente, String  idDetinatario){
        mensagemDir = mensagemRef.child(idRemetente).child(idDetinatario);
        isPrepare = true;
    }
}
