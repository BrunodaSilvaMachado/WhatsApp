package com.cursoandroid.whatsapp.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cursoandroid.whatsapp.R;
import com.cursoandroid.whatsapp.adapter.ContatosAdapter;
import com.cursoandroid.whatsapp.adapter.GrupoSeleciondoAdapter;
import com.cursoandroid.whatsapp.model.Usuario;
import com.cursoandroid.whatsapp.serviceImpl.UsuarioService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SelecionarMembrosGrupoFragment extends Fragment {

    private final List<Usuario> membrosList = new ArrayList<>();
    private final List<Usuario> membrosSelecionadosList = new ArrayList<>();
    private final UsuarioService usuarioService = new UsuarioService();
    private ContatosAdapter contatosAdapter;
    private GrupoSeleciondoAdapter grupoSeleciondoAdapter;
    private final String UEMAIL = UsuarioService.getCurrentUser().getEmail();
    private Toolbar toolbar;
    public SelecionarMembrosGrupoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_selecionar_membros_grupo, container, false);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        toolbar = view.findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle(R.string.new_group);

        if (appCompatActivity != null) {
            appCompatActivity.setSupportActionBar(toolbar);
            if (appCompatActivity.getSupportActionBar() != null){
                appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        FloatingActionButton fab = view.findViewById(R.id.fabAvancarCadastro);
        RecyclerView rvMembrosSelecionados = view.findViewById(R.id.rv_membros_selecionados);
        RecyclerView rvMembros = view.findViewById(R.id.rv_membros);

        contatosAdapter = new ContatosAdapter(getContext(), membrosList, u -> {
            membrosList.remove(u);
            contatosAdapter.notifyDataSetChanged();
            membrosSelecionadosList.add(u);
            grupoSeleciondoAdapter.notifyDataSetChanged();
            atulizarMembrosToolbar();
        });
        rvMembros.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMembros.setHasFixedSize(true);
        rvMembros.setAdapter(contatosAdapter);
        grupoSeleciondoAdapter = new GrupoSeleciondoAdapter(getContext(), membrosSelecionadosList, u -> {
            membrosSelecionadosList.remove(u);
            grupoSeleciondoAdapter.notifyDataSetChanged();
            membrosList.add(u);
            contatosAdapter.notifyDataSetChanged();
            atulizarMembrosToolbar();
        });
        rvMembrosSelecionados.setLayoutManager(new LinearLayoutManager(
                getContext(), LinearLayoutManager.HORIZONTAL, false
        ));
        rvMembrosSelecionados.setHasFixedSize(true);
        rvMembrosSelecionados.setAdapter(grupoSeleciondoAdapter);

        fab.setOnClickListener(v->{
            if (membrosSelecionadosList.size() > 0){
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("membrosSelecionados", (ArrayList<Usuario>) membrosSelecionadosList);
                NavHostFragment.findNavController(SelecionarMembrosGrupoFragment.this)
                        .navigate(R.id.action_selecionarMembrosGrupoFragment_to_cadastrarMembrosGrupoFragment, bundle);
            } else{
                Toast.makeText(getContext(), R.string.no_members, Toast.LENGTH_LONG).show();
            }

        });
    }

    @Override
    public void onStop() {
        usuarioService.stop();
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarContatos();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        usuarioService.finish();
    }

    private void recuperarContatos() {

        usuarioService.setUsuarioEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                membrosList.clear();
                for (DataSnapshot dados : snapshot.getChildren()) {
                    Usuario usuario = dados.getValue(Usuario.class);
                    if (usuario != null && !UEMAIL.equals(usuario.getEmail())) {
                        membrosList.add(usuario);
                    }
                }
                contatosAdapter.notifyDataSetChanged();
                atulizarMembrosToolbar();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {}
        });
        usuarioService.start();
    }

    private void atulizarMembrosToolbar() {
        int totalSelecionados = membrosSelecionadosList.size();
        int total = membrosList.size() + totalSelecionados;
        toolbar.setSubtitle(String.format(Locale.getDefault(), "%d %s %d %s",
                totalSelecionados, getString(R.string.of), total, getString(R.string.selected)));
    }
}