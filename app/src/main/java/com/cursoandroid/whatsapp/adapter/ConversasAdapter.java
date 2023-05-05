package com.cursoandroid.whatsapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.View;
import androidx.annotation.NonNull;
import com.bumptech.glide.Glide;
import com.cursoandroid.whatsapp.R;
import com.cursoandroid.whatsapp.activity.ChatActivity;
import com.cursoandroid.whatsapp.model.Conversa;
import com.cursoandroid.whatsapp.model.Grupo;
import com.cursoandroid.whatsapp.model.Usuario;
import com.cursoandroid.whatsapp.serviceImpl.ConversaService;

import java.io.Serializable;
import java.util.List;

public class ConversasAdapter extends ContentAdapter<Conversa> {
    public ConversasAdapter(Context context, List<Conversa> conversas) {
        super(context, conversas);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Conversa conversa = tList.get(position);
        Usuario usuario = null;
        Grupo grupo;
        String titulo = "";
        String fotoUrl = null;

        if (conversa.isGrupoConversa()){
            grupo = conversa.getGrupo();
            titulo = grupo.getNome();
            fotoUrl = grupo.getFoto();

        }else if((usuario = conversa.getUsuarioExibicao()) != null){
            titulo = usuario.getNome();
            fotoUrl = usuario.getFoto();
        }
        holder.titulo.setText(titulo);
        if (fotoUrl != null) {
            Glide.with(context).load(Uri.parse(fotoUrl)).into(holder.foto);
        }
        else{
            holder.foto.setImageResource(R.drawable.padrao);
        }
        holder.subtitulo.setText(conversa.getUltimaMensagem());
        if (conversa.isEnfase()){
            holder.subtitulo.setTypeface(holder.subtitulo.getTypeface(), Typeface.BOLD_ITALIC);
        }

        if (!conversa.isCheck()){
            holder.notificacao.setVisibility(View.VISIBLE);
        }
        Usuario finalUsuario = usuario;
        holder.itemView.setOnClickListener(view -> {
            if (!conversa.isCheck()){
                ConversaService conversaService = new ConversaService();
                conversa.setCheck(true);
                conversaService.update(conversa);
            }

            Intent i = new Intent(context, ChatActivity.class);
            if (conversa.isGrupoConversa()){
                i.putExtra("chatGrupo", conversa.getGrupo());
            }else {
                i.putExtra("chatContato", (Serializable) finalUsuario);
            }

            context.startActivity(i);
        });
    }

}
