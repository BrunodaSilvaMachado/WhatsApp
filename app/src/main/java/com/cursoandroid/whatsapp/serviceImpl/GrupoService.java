package com.cursoandroid.whatsapp.serviceImpl;

import androidx.annotation.NonNull;
import com.cursoandroid.whatsapp.config.FirebaseConfig;
import com.cursoandroid.whatsapp.helper.Base64Custom;
import com.cursoandroid.whatsapp.model.Conversa;
import com.cursoandroid.whatsapp.model.Grupo;
import com.cursoandroid.whatsapp.model.Usuario;
import com.cursoandroid.whatsapp.service.Service;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;

public class GrupoService implements Service {

    private static final String TAG = GrupoService.class.getSimpleName();
    private static DatabaseReference grupoRef = null;
    private final ConversaService conversaService = new ConversaService();
    //private DatabaseReference mGrupos = null;
    private ChildEventListener eventListener = null;

    public GrupoService(){
        if (grupoRef == null){
            grupoRef = FirebaseConfig.getDatabaseReference().child("grupos");
            //mGrupos = grupoRef.child("");
        }

    }
    @Override
    public void start() {
        if (grupoRef != null) {
            grupoRef.addChildEventListener(eventListener);
        }
    }

    @Override
    public void restart() {
        stop();
        start();
    }

    @Override
    public void stop() {
        if (grupoRef != null) {
            grupoRef.removeEventListener(eventListener);
        }
    }

    @Override
    public void finish() {
        grupoRef = null;
        //mGrupos = null;
    }

    public void save(@NonNull Grupo grupo){
        grupoRef.child(grupo.getId()).setValue(grupo);
        for (Usuario membro: grupo.getMembros()){
            String idRemetente = Base64Custom.encoder(membro.getEmail());
            Conversa conversa = new Conversa(idRemetente, grupo.getId(),"",false,null,false,grupo,true);
            conversaService.save(conversa);
        }
    }

    public void setEventListener(ChildEventListener eventListener){
        this.eventListener = eventListener;
    }

    public Grupo newGrupoWithId() {
        Grupo grupo = new Grupo();
        grupo.setId(grupoRef.push().getKey());
        return grupo;
    }
}
