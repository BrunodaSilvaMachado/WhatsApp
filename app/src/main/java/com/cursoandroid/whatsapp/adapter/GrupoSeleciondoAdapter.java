package com.cursoandroid.whatsapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.cursoandroid.whatsapp.R;
import com.cursoandroid.whatsapp.model.Usuario;
import de.hdodenhof.circleimageview.CircleImageView;

import java.util.List;

public class GrupoSeleciondoAdapter extends RecyclerView.Adapter<GrupoSeleciondoAdapter.MyViewHolder>{
    private final Context context;
    private final List<Usuario> contatosSelecionados;
    private final OnItemClickListener<Usuario> itemClickListener;

    public GrupoSeleciondoAdapter(Context context, List<Usuario> usuarios, OnItemClickListener<Usuario> itemClickListener) {
        this.context = context;
        contatosSelecionados = usuarios;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_grupo_selecionado, parent, false);
        return new MyViewHolder(itemLista);
    }

    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Usuario usuario = contatosSelecionados.get(position);
        holder.titulo.setText(usuario.getNome());
        holder.itemView.setOnClickListener(v->itemClickListener.onItemClick(usuario));
        if (usuario.getFoto() != null) {
            Glide.with(context).load(Uri.parse(usuario.getFoto())).into(holder.foto);
        }else {
            holder.foto.setImageResource(R.drawable.padrao);
        }
    }

    @Override
    public int getItemCount() {
        return contatosSelecionados.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView foto;
        TextView titulo;

        public MyViewHolder(View itemView) {
            super(itemView);

            titulo = itemView.findViewById(R.id.tv_membros_selecionado);
            foto = itemView.findViewById(R.id.iv_foto_membro_selecionado);
        }
    }
}
