package com.cursoandroid.whatsapp.serviceImpl;

import android.net.Uri;
import android.util.Log;
import androidx.annotation.NonNull;
import com.cursoandroid.whatsapp.R;
import com.cursoandroid.whatsapp.config.FirebaseConfig;
import com.cursoandroid.whatsapp.helper.Base64Custom;
import com.cursoandroid.whatsapp.model.Usuario;
import com.cursoandroid.whatsapp.service.Service;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UsuarioService implements Service {
    private static final String TAG = UsuarioService.class.getSimpleName();
    private static final FirebaseAuth auth = FirebaseConfig.getFirebaseAuth();
    private static DatabaseReference usuarioRef = null;
    private ValueEventListener allUsersEventListener;

    public UsuarioService(){
        if (usuarioRef == null) {
            usuarioRef = FirebaseConfig.getDatabaseReference().child("usuarios");
        }
    }

    public void start(){
        if (allUsersEventListener != null) {
            usuarioRef.addValueEventListener(allUsersEventListener);
        }
    }

    @Override
    public void restart() {
        stop();
        start();
    }

    public void stop(){
        if (usuarioRef != null && allUsersEventListener != null){
                usuarioRef.removeEventListener(allUsersEventListener);
        }
    }

    @Override
    public void finish() {
        usuarioRef = null;
    }

    @NonNull
    public static String getCurrentUserId(){
        FirebaseUser user =  auth.getCurrentUser();
        if (user != null)
            return Base64Custom.encoder(Objects.requireNonNull(user.getEmail()));
        else
            return "";
    }
    @NonNull
    public static Usuario getCurrentUser(){
        FirebaseUser user =  auth.getCurrentUser();
         if (user == null) {
             throw new RuntimeException(TAG+": user not found or disconnect");
         }
        Usuario usuario = new Usuario(
                user.getDisplayName(),
                user.getEmail(),
                null
        );
        usuario.setId(getCurrentUserId());
        Uri url = user.getPhotoUrl();
        if (url != null) {
            usuario.setFoto(url.toString());
        }
        return usuario;
    }

    public void save(@NonNull Usuario usuario){
        usuarioRef.child(usuario.getId()).setValue(usuario);
        updateUserName(usuario.getNome());
    }

    public void update(@NonNull Usuario usuario){
        Map<String, Object> mUser = new HashMap<>();
        mUser.put("nome", usuario.getNome());
        mUser.put("email", usuario.getEmail());
        mUser.put("foto", usuario.getFoto());
        usuarioRef.child(usuario.getId()).updateChildren(mUser);
    }

    public void delete(@NonNull Usuario usuario) {
        usuarioRef.child(usuario.getId()).removeValue();
    }

    public void updateUserPhoto(Uri uri){
        try{
            FirebaseUser user = auth.getCurrentUser();
            if (user != null) {
                UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                        .setPhotoUri(uri).build();
                user.updateProfile(profile).addOnCompleteListener(task->{
                    if (!task.isSuccessful()){
                        Log.d(TAG, String.valueOf(R.string.upload_failed));
                    }
                });
            }

        }catch (Exception ignored){}
    }

    public void updateUserName(String name){
        try{
            FirebaseUser user = auth.getCurrentUser();
            if (user != null) {
                UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                        .setDisplayName(name).build();
                user.updateProfile(profile).addOnCompleteListener(task->{
                    if (!task.isSuccessful()){
                        Log.d(TAG, String.valueOf(R.string.invalid_username));
                    }
                });
            }
        }catch (Exception ignored){}
    }

    public void updateUserProfile(String name, Uri photo){
        Usuario usuario = getCurrentUser();
        if (name != null) {
            usuario.setNome(name);
            updateUserName(name);
        }
        if (photo != null){
            usuario.setFoto(photo.toString());
            updateUserPhoto(photo);
        }
        update(usuario);
    }

    public void setUsuarioEventListener(ValueEventListener usuarioEventListener){
        this.allUsersEventListener = usuarioEventListener;
    }
}
