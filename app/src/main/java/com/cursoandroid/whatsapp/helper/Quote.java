package com.cursoandroid.whatsapp.helper;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.bumptech.glide.Glide;
import com.cursoandroid.whatsapp.R;
import com.cursoandroid.whatsapp.model.Mensagem;

public class Quote {
    public static void toQuote(Context context, @NonNull  View view, @NonNull Mensagem mensagem){
        final TextView name = view.findViewById(R.id.tv_name);
        final ImageView imgQuote = view.findViewById(R.id.iv_img_quote);
        final TextView msgQuote = view.findViewById(R.id.tv_msg_quote);
        if(mensagem.getNome() != null){
            name.setText(mensagem.getNome());
        }
        if (mensagem.getImagem() != null) {
            Glide.with(context).load(Uri.parse(mensagem.getImagem())).into(imgQuote);
            msgQuote.setVisibility(View.GONE);
        }else{
            msgQuote.setText(mensagem.getMensagem());
            imgQuote.setVisibility(View.GONE);
        }
    }
}
