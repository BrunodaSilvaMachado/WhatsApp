package com.cursoandroid.whatsapp.serviceImpl;

import android.util.Log;
import androidx.annotation.NonNull;
import com.cursoandroid.whatsapp.config.FirebaseConfig;
import com.cursoandroid.whatsapp.model.Conversa;
import com.cursoandroid.whatsapp.service.Service;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ConversaService implements Service {
    private static final String TAG = ConversaService.class.getSimpleName();
    private static DatabaseReference conversaRef = null;
    private DatabaseReference conversasUser;
    private ChildEventListener eventListener = null;
    public ConversaService(){
        if (conversaRef == null){
            conversaRef = FirebaseConfig.getDatabaseReference().child("conversas");
        }

        try{
            conversasUser = conversaRef.child(Objects.requireNonNull(UsuarioService.getCurrentUserId()));
        } catch (Exception exception){
            conversasUser = null;
            Log.e(TAG, exception.getMessage());
        }
    }

    @Override
    public void start() {
        if (conversasUser != null) {
            conversasUser.addChildEventListener(eventListener);
        }
    }

    @Override
    public void restart() {
        stop();
        start();
    }

    @Override
    public void stop() {
        if (conversasUser != null) {
            conversasUser.removeEventListener(eventListener);
        }
    }

    @Override
    public void finish() {
        conversaRef = null;
        conversasUser = null;
    }

    public void save(@NonNull Conversa conversa){
        conversaRef.child(conversa.getIdRemetente()).child(conversa.getIdDestinatario()).setValue(conversa);
    }

    public void update(@NonNull Conversa conversa){
        Map<String, Object> mUser = new HashMap<>();
        mUser.put("idRemetente", conversa.getIdRemetente());
        mUser.put("idDestinatario", conversa.getIdDestinatario());
        mUser.put("ultimaMensagem", conversa.getUltimaMensagem());
        mUser.put("usuarioExibicao", conversa.getUsuarioExibicao());
        mUser.put("check", conversa.isCheck());
        conversaRef.child(conversa.getIdRemetente()).child(conversa.getIdDestinatario()).updateChildren(mUser);
    }

    public void setEventListener(ChildEventListener eventListener){
        this.eventListener = eventListener;
    }
}
