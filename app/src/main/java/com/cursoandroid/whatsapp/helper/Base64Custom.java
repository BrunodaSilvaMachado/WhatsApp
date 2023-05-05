package com.cursoandroid.whatsapp.helper;


import android.util.Base64;
import androidx.annotation.NonNull;

public class Base64Custom {

    @NonNull
    public static String encoder(@NonNull String text){
        return Base64.encodeToString(text.getBytes(), Base64.DEFAULT)
                .replaceAll("(\\n|\\r)","");
    }

    @NonNull
    public static String decoder(String encoderText){
        return new String(Base64.decode(encoderText,Base64.DEFAULT));
    }
}
