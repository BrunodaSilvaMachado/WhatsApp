package com.cursoandroid.whatsapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.cursoandroid.whatsapp.R;
import de.hdodenhof.circleimageview.CircleImageView;

import java.util.List;

public abstract class ContentAdapter<T> extends RecyclerView.Adapter<ContentAdapter.MyViewHolder> {
    List<T> tList;
    Context context;

    public ContentAdapter(Context context, List<T> tList) {
        this.tList = tList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_content, parent, false);
        return new MyViewHolder(itemLista);
    }

    public abstract void onBindViewHolder(@NonNull MyViewHolder holder, int position);

    @Override
    public int getItemCount() {
        return tList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView foto;
        ImageView notificacao;
        TextView titulo, subtitulo;

        public MyViewHolder(View itemView) {
            super(itemView);

            titulo = itemView.findViewById(R.id.tv_membros_selecionado);
            notificacao = itemView.findViewById(R.id.iv_notification);
            subtitulo = itemView.findViewById(R.id.textAdapterSubtitulo);
            foto = itemView.findViewById(R.id.iv_foto_membro_selecionado);
        }
    }
}
