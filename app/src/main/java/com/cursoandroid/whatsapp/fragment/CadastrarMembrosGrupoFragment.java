package com.cursoandroid.whatsapp.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cursoandroid.whatsapp.R;
import com.cursoandroid.whatsapp.activity.ChatActivity;
import com.cursoandroid.whatsapp.adapter.GrupoSeleciondoAdapter;
import com.cursoandroid.whatsapp.config.FirebaseConfig;
import com.cursoandroid.whatsapp.helper.ImageLifecycleObserver;
import com.cursoandroid.whatsapp.helper.UploadPhoto;
import com.cursoandroid.whatsapp.model.Grupo;
import com.cursoandroid.whatsapp.model.Usuario;
import com.cursoandroid.whatsapp.serviceImpl.GrupoService;
import com.cursoandroid.whatsapp.serviceImpl.UsuarioService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.StorageReference;
import de.hdodenhof.circleimageview.CircleImageView;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CadastrarMembrosGrupoFragment extends Fragment {
    private final GrupoService grupoService = new GrupoService();
    private ImageLifecycleObserver imageFromGallery;
    private List<Usuario> membrosSelecionados = new ArrayList<>();
    private CircleImageView ivImgGrupo;
    private Grupo grupo;

    public CadastrarMembrosGrupoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null){
            imageFromGallery = new ImageLifecycleObserver(getActivity(), this::setImageGrupo);
            getLifecycle().addObserver(imageFromGallery);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cadastrar_membros_grupo, container, false);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        Toolbar toolbar = view.findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle(R.string.new_group);
        toolbar.setSubtitle(R.string.set_name);

        if (appCompatActivity != null) {
            appCompatActivity.setSupportActionBar(toolbar);
            if (appCompatActivity.getSupportActionBar() != null){
                appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
        Bundle bundle = getArguments();
        if (bundle != null){
            membrosSelecionados = bundle.getParcelableArrayList("membrosSelecionados");
        }

        grupo = grupoService.newGrupoWithId();

        TextView numParticipants = view.findViewById(R.id.num_participants);
        numParticipants.setText(String.valueOf(membrosSelecionados.size()));

        RecyclerView rvMembrosGrupo = view.findViewById(R.id.rv_membros_grupo);
        ivImgGrupo = view.findViewById(R.id.iv_img_grupo);
        ivImgGrupo.setOnClickListener(v-> imageFromGallery.selectImage());
        configuararRecyclerView(rvMembrosGrupo);
        configurarSalvarGrupo(view);
    }

    private void configurarSalvarGrupo(@NonNull View view) {
        FloatingActionButton fabFinalizar = view.findViewById(R.id.fab_finalizar_cadastro);
        TextView etNomeGrupo = view.findViewById(R.id.et_nome_grupo);
        fabFinalizar.setOnClickListener(l->{
            String nomeGrupo = etNomeGrupo.getText().toString();
            if (!nomeGrupo.isEmpty()) {
                membrosSelecionados.add(UsuarioService.getCurrentUser());
                grupo.setMembros(membrosSelecionados);
                grupo.setNome(nomeGrupo);
                grupoService.save(grupo);
                if (getActivity() != null){
                    Intent i = new Intent(getActivity(), ChatActivity.class);
                    i.putExtra("chatGrupo", grupo);
                    startActivity(i);
                }
            } else{
                Toast.makeText(getContext(), R.string.set_name, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void configuararRecyclerView(@NonNull RecyclerView rvMembrosGrupo){
        GrupoSeleciondoAdapter grupoSeleciondoAdapter = new GrupoSeleciondoAdapter(getContext(), membrosSelecionados, u -> {
        });

        rvMembrosGrupo.setLayoutManager(new LinearLayoutManager(
                getContext(), LinearLayoutManager.HORIZONTAL, false
        ));
        rvMembrosGrupo.setHasFixedSize(true);
        rvMembrosGrupo.setAdapter(grupoSeleciondoAdapter);
    }

    void setImageGrupo(Bitmap bitmap) {
        StorageReference storageReference = FirebaseConfig.getStorageReference();
        if (bitmap != null) {
            ivImgGrupo.setImageBitmap(bitmap);
            //Salvar imagem no firebase
            final StorageReference imageRef = storageReference.child("imagens").child("grupos")
                    .child(grupo.getNome() + ".jpeg");

            if (getActivity() != null) {
                UploadPhoto.upload(getActivity(), imageRef,
                        bitmap, task -> grupo.setFoto(task.getResult().toString()));
            }

        }
    }
}