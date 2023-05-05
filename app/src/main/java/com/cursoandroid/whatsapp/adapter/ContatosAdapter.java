package com.cursoandroid.whatsapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import androidx.annotation.NonNull;
import com.bumptech.glide.Glide;
import com.cursoandroid.whatsapp.R;
import com.cursoandroid.whatsapp.model.Usuario;

import java.util.List;

public class ContatosAdapter extends ContentAdapter<Usuario> {

    OnItemClickListener<Usuario> onItemClickListener;
    public ContatosAdapter(Context context, List<Usuario> contatos, OnItemClickListener<Usuario> itemClickListener) {
        super(context, contatos);
        this.onItemClickListener = itemClickListener;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Usuario usuario = tList.get(position);
        boolean cabecalho = usuario.getEmail().isEmpty();

        holder.titulo.setText(usuario.getNome());
        holder.subtitulo.setText(usuario.getEmail());
        if (usuario.getFoto() != null) {
            Glide.with(context).load(Uri.parse(usuario.getFoto())).into(holder.foto);
        }
        else{
            if(cabecalho){
                holder.foto.setImageResource(R.drawable.icone_grupo);
                holder.subtitulo.setVisibility(View.GONE);
            }else {
                holder.foto.setImageResource(R.drawable.padrao);
            }
        }
        holder.itemView.setOnClickListener(view -> onItemClickListener.onItemClick(usuario));
    }
}
