package com.cursoandroid.whatsapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.cursoandroid.whatsapp.R;
import com.cursoandroid.whatsapp.activity.ImageChatActivity;
import com.cursoandroid.whatsapp.helper.Quote;
import com.cursoandroid.whatsapp.model.Mensagem;
import com.cursoandroid.whatsapp.serviceImpl.UsuarioService;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MensagensAdapter extends RecyclerView.Adapter<MensagensAdapter.MyViewHolder> {
    private final int TIPO_REMETENTE = 0;
    private final int TIPO_DESTINATARIO = 1;
    private final List<Mensagem> mensagemList;
    private final Context context;
    private final OnItemClickListener<Mensagem> itemClickListener;

    public MensagensAdapter(Context context, List<Mensagem> mensagens, OnItemClickListener<Mensagem> itemClickListener){
        this.context = context;
        this.mensagemList = mensagens;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(
                        (viewType == TIPO_REMETENTE)? R.layout.adapter_mensagem_remetente:
                                R.layout.adapter_mensagem_destinatario,
                        parent,false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        Mensagem mensagem = mensagemList.get(position);
        String msg = mensagem.getMensagem();
        String imagem = mensagem.getImagem();
        String name = mensagem.getNome();
        Mensagem quote = mensagem.getQuote();

        if (!name.isEmpty()){
            holder.nome.setText(name);
        }else{
            holder.nome.setVisibility(View.GONE);
        }
        if (imagem != null){
            Glide.with(context).load(Uri.parse(imagem)).into(holder.image);
            holder.message.setVisibility(View.GONE);
            holder.image.setOnClickListener(v->{
                Intent i = new Intent(context, ImageChatActivity.class);
                i.putExtra("IMAGE_ZOOM", imagem);
                i.putExtra("NAME_PROP",name);
                context.startActivity(i);
            });
        }else {
            holder.message.setText(msg);
            holder.image.setVisibility(View.GONE);
        }
        holder.itemView.setOnLongClickListener(l->{
            itemClickListener.onItemClick(mensagem);
            return true;
        });

        if (quote != null) {
            Quote.toQuote(context, holder.layoutQuote, quote);
        }else {
            holder.layoutQuote.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mensagemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Mensagem mensagem = mensagemList.get(position);
        String idUsuario = UsuarioService.getCurrentUserId();

        return (idUsuario.equals(mensagem.getIdUsuario()))? TIPO_REMETENTE: TIPO_DESTINATARIO;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView nome;
        TextView message;
        ImageView image;
        LinearLayout layoutQuote;
        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            nome = itemView.findViewById(R.id.tv_msg_titulo);
            message = itemView.findViewById(R.id.tv_msg_texto);
            image = itemView.findViewById(R.id.iv_msg_foto);
            layoutQuote = itemView.findViewById(R.id.layout_quote);
        }
    }
}
