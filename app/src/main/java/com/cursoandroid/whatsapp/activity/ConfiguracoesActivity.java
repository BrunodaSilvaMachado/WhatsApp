package com.cursoandroid.whatsapp.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.bumptech.glide.Glide;
import com.cursoandroid.whatsapp.R;
import com.cursoandroid.whatsapp.config.FirebaseConfig;
import com.cursoandroid.whatsapp.helper.ImageCapture;
import com.cursoandroid.whatsapp.helper.Permissoes;
import com.cursoandroid.whatsapp.helper.UploadPhoto;
import com.cursoandroid.whatsapp.model.Usuario;
import com.cursoandroid.whatsapp.serviceImpl.UsuarioService;
import com.google.firebase.storage.StorageReference;
import de.hdodenhof.circleimageview.CircleImageView;

public class ConfiguracoesActivity extends AppCompatActivity {
    private final ActivityResultLauncher<Intent> imageFromCameraLauncher = ImageCapture.registerCameraForActivityResult(this, this::setProfileImage);
    private final ActivityResultLauncher<String> imageFromGalleryLaucher = ImageCapture.registerGalleryForActivityResult(this, this::setProfileImage);
    private final UsuarioService usuarioService = new UsuarioService();
    private EditText editPersonName;
    private CircleImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        String[] permissoesNecessarias = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        };

        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle(R.string.settings);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //validar permissões
        if (Build.VERSION.SDK_INT >= 23) {
            Permissoes.validarPermissoes(this).launch(permissoesNecessarias);
        }

        profileImage = findViewById(R.id.iv_foto_membro_selecionado);
        editPersonName = findViewById(R.id.et_person_name);
        ImageButton btnCamera = findViewById(R.id.btn_photo);
        ImageButton btnGallery = findViewById(R.id.btn_gallery);
        ImageView atualizaNome = findViewById(R.id.iv_atualiza_nome);
        btnCamera.setOnClickListener(v -> imageFromCameraLauncher.launch(new Intent(MediaStore.ACTION_IMAGE_CAPTURE)));
        btnGallery.setOnClickListener(v -> imageFromGalleryLaucher.launch("image/*"));
        atualizaNome.setOnClickListener(v -> usuarioService.updateUserProfile(editPersonName.getText().toString(), null));
        loadProfile();
    }

    @Override
    protected void onStart() {
        super.onStart();
        usuarioService.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        usuarioService.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        usuarioService.finish();
    }

    void setProfileImage(Bitmap bitmap) {
        StorageReference storageReference = FirebaseConfig.getStorageReference();
        if (bitmap != null) {
            profileImage.setImageBitmap(bitmap);
            //Salvar imagem no firebase
            final StorageReference imageRef = storageReference.child("imagens").child("perfil")
                    .child(UsuarioService.getCurrentUserId() + ".jpeg");
            UploadPhoto.upload(ConfiguracoesActivity.this, imageRef,
                    bitmap, task -> updateUserPhoto(task.getResult()));
        }
    }

    private void loadProfile() {
        Usuario user = UsuarioService.getCurrentUser();
        String url = user.getFoto();
        if (url != null) {
            Glide.with(this).load(Uri.parse(url)).into(profileImage);
        } else {
            profileImage.setImageResource(R.drawable.padrao);
        }
        editPersonName.setText(user.getNome());
    }

    private void updateUserPhoto(Uri uri) {
        usuarioService.updateUserProfile(null, uri);
    }
}