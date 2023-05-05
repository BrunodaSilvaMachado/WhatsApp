package com.cursoandroid.whatsapp.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.cursoandroid.whatsapp.R;
import com.cursoandroid.whatsapp.adapter.MensagensAdapter;
import com.cursoandroid.whatsapp.config.FirebaseConfig;
import com.cursoandroid.whatsapp.databinding.ActivityChatBinding;
import com.cursoandroid.whatsapp.helper.*;
import com.cursoandroid.whatsapp.model.Conversa;
import com.cursoandroid.whatsapp.model.Grupo;
import com.cursoandroid.whatsapp.model.Mensagem;
import com.cursoandroid.whatsapp.model.Usuario;
import com.cursoandroid.whatsapp.serviceImpl.ConversaService;
import com.cursoandroid.whatsapp.serviceImpl.MensagemService;
import com.cursoandroid.whatsapp.serviceImpl.UsuarioService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.storage.StorageReference;
import de.hdodenhof.circleimageview.CircleImageView;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {

    private final MensagemService mensagemService = new MensagemService();
    private final ConversaService conversaService = new ConversaService();
    private final String idUsuarioRemetente = UsuarioService.getCurrentUserId();
    private final Usuario usuarioRemetente = UsuarioService.getCurrentUser();
    private final List<Mensagem> mensagemList = new ArrayList<>();
    private Usuario usuarioDestinatario;
    private ActivityChatBinding binding;
    private String idUsuarioDestinatario;
    private MensagensAdapter mensagensAdapter;
    private Grupo grupo;
    private final ActivityResultLauncher<Intent> imageFromCameraLauncher = ImageCapture.registerCameraForActivityResult(this, this::setProfileImage);
    private final ActivityResultLauncher<String> imageFromGalleryLaucher = ImageCapture.registerGalleryForActivityResult(this, this::setProfileImage);
    private LinearLayout layoutQuote;
    private Mensagem mensagemQuote = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.toolbar.setTitle("");
        setSupportActionBar(binding.toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (Build.VERSION.SDK_INT >= 23) {
            Permissoes.validarPermissoes(this).launch(new String[]{Manifest.permission.CAMERA});
        }

        ImageView imgCamera = findViewById(R.id.iv_camera);
        ImageView imgGallery = findViewById(R.id.iv_gallery);
        EditText etMsg = findViewById(R.id.et_msg);
        layoutQuote = findViewById(R.id.layout_quote);
        ImageView imgCancel = findViewById(R.id.iv_cancel_quote);
        FloatingActionButton fabEnviar = findViewById(R.id.fab_enviar);
        imgCamera.setOnClickListener(v -> imageFromCameraLauncher.launch(new Intent(MediaStore.ACTION_IMAGE_CAPTURE)));
        imgGallery.setOnClickListener(v -> imageFromGalleryLaucher.launch("image/*"));
        fabEnviar.setOnClickListener(v -> {
            String txtMensagem = etMsg.getText().toString();
            if (!txtMensagem.isEmpty()) {
                enviarMensagem(txtMensagem, null, mensagemQuote, false);
                etMsg.setText("");
            }
            layoutQuote.setVisibility(View.GONE);
            mensagemQuote = null;
        });
        layoutQuote.setVisibility(View.GONE);
        imgCancel.setOnClickListener(l-> layoutQuote.setVisibility(View.GONE));
        configurarBarraTitulo();
        configurarListarMensagem();
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarMensagens();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mensagemService.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mensagemService.finish();
    }

    void configurarBarraTitulo() {
        final TextView tvNome = binding.tvNomeChat;
        final CircleImageView ciFoto = binding.ciFotoChat;
        //Recuperar dados do usuário
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String foto;
            String nome;
            if (bundle.containsKey("chatGrupo")) {
                grupo = (Grupo) bundle.getSerializable("chatGrupo");
                idUsuarioDestinatario = grupo.getId();
                foto = grupo.getFoto();
                nome = grupo.getNome();
            } else {
                usuarioDestinatario = (Usuario) bundle.getSerializable("chatContato");
                idUsuarioDestinatario = Base64Custom.encoder(usuarioDestinatario.getEmail());
                foto = usuarioDestinatario.getFoto();
                nome = usuarioDestinatario.getNome();
            }

            tvNome.setText(nome);
            if (foto != null) {
                Glide.with(this).load(Uri.parse(foto)).into(ciFoto);
            }
        }

    }

    void configurarListarMensagem() {
        mensagensAdapter = new MensagensAdapter(this, mensagemList,this::apagarMensagem);
        RecyclerView recyclerMsg = findViewById(R.id.recycler_mensagens);
        recyclerMsg.setHasFixedSize(true);
        recyclerMsg.setAdapter(mensagensAdapter);
        swipe(recyclerMsg);
    }

    private void recuperarMensagens() {
        mensagemList.clear();
        mensagemService.setEventListener(new ChildEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                Mensagem mensagem = snapshot.getValue(Mensagem.class);
                if (mensagem != null) {
                    mensagem.setId(snapshot.getKey());
                    mensagemList.add(mensagem);
                    mensagensAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {}

            @Override
            public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) {}

            @Override
            public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {}

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {}
        });
        mensagemService.prepare(idUsuarioRemetente, idUsuarioDestinatario);
        mensagemService.start();
    }

    void setProfileImage(Bitmap bitmap) {
        StorageReference storageReference = FirebaseConfig.getStorageReference();
        if (bitmap != null) {
            //Salvar imagem no firebase
            final StorageReference imageRef = storageReference.child("imagens").child("fotos")
                    .child(idUsuarioRemetente).child(UUID.randomUUID().toString());

            UploadPhoto.upload(ChatActivity.this, imageRef,
                    bitmap, task -> {
                        enviarMensagem(getString(R.string.msg_image), task.getResult().toString(), mensagemQuote,true);
                        mensagemQuote = null;
                    });
        }
    }

    private void enviarMensagem(String txtMensagem, String imgMensagem, Mensagem quote, boolean enfase){
        Mensagem mensagem = new Mensagem(idUsuarioRemetente, txtMensagem, imgMensagem);
        mensagem.setQuote(quote);
        if (usuarioDestinatario != null) {
            mensagemService.save(idUsuarioRemetente, idUsuarioDestinatario, mensagem);
            mensagemService.save(idUsuarioDestinatario, idUsuarioRemetente, mensagem);
            //Salvar conversa remetente
            conversaService.save(new Conversa(idUsuarioRemetente, idUsuarioDestinatario,
                    mensagem.getMensagem(), enfase, usuarioDestinatario, true, null, false));
            //Salvar conversa destinatário
            conversaService.save(new Conversa(idUsuarioDestinatario, idUsuarioRemetente,
                    mensagem.getMensagem(), enfase,  usuarioRemetente, false, null, false));
        } else {
            for (Usuario membro : grupo.getMembros()) {
                String idRemetenteGrupo = Base64Custom.encoder(membro.getEmail());
                mensagem.setNome(usuarioRemetente.getNome());
                mensagemService.save(idRemetenteGrupo, idUsuarioDestinatario, mensagem);
                conversaService.save(new Conversa(idRemetenteGrupo, idUsuarioDestinatario, mensagem.getMensagem(), enfase,
                        null, false, grupo, true));
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void apagarMensagem(Mensagem mensagem){
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
        builder.setTitle(R.string.delete)
                .setMessage(R.string.confirm_deletion)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, (w, d)->{
                    if (usuarioDestinatario != null) {
                        mensagemService.delete(idUsuarioRemetente, idUsuarioDestinatario, mensagem);
                        mensagemService.delete(idUsuarioDestinatario, idUsuarioRemetente, mensagem);
                        //Salvar conversa remetente
                        conversaService.save(new Conversa(idUsuarioRemetente, idUsuarioDestinatario,
                                getString(R.string.msg_delete), true, usuarioDestinatario, true, null, false));
                        //Salvar conversa destinatário
                        conversaService.save(new Conversa(idUsuarioDestinatario, idUsuarioRemetente,
                                getString(R.string.msg_delete), true,  usuarioRemetente, false, null, false));
                    } else {
                        for (Usuario membro : grupo.getMembros()) {
                            String idRemetenteGrupo = Base64Custom.encoder(membro.getEmail());
                            mensagem.setNome(usuarioRemetente.getNome());
                            mensagemService.delete(idRemetenteGrupo, idUsuarioDestinatario, mensagem);
                            conversaService.save(new Conversa(idRemetenteGrupo, idUsuarioDestinatario, getString(R.string.msg_delete), true,
                                    null, false, grupo, true));
                        }
                    }
                    mensagemList.remove(mensagem);
                    mensagensAdapter.notifyDataSetChanged();
                }).setNegativeButton(android.R.string.cancel, null)
                .create().show();
    }

    private void swipe(RecyclerView recyclerView) {
        ItemTouchHelper.Callback itemTouch = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.ACTION_STATE_IDLE;
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                mensagemQuote = mensagemList.get(viewHolder.getAdapterPosition()) ;
                Quote.toQuote(getApplicationContext(), layoutQuote, mensagemQuote);
                layoutQuote.setVisibility(View.VISIBLE);
            }
        };

        new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerView);
    }

}