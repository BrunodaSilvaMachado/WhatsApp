package com.cursoandroid.whatsapp.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cursoandroid.whatsapp.R;
import com.cursoandroid.whatsapp.activity.ChatActivity;
import com.cursoandroid.whatsapp.activity.GrupoActivity;
import com.cursoandroid.whatsapp.adapter.ContatosAdapter;
import com.cursoandroid.whatsapp.adapter.OnItemClickListener;
import com.cursoandroid.whatsapp.model.Usuario;
import com.cursoandroid.whatsapp.serviceImpl.UsuarioService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContatosFragment extends Fragment implements SearchArrayList {

    private final UsuarioService usuarioService = new UsuarioService();
    private final String UEMAIL = UsuarioService.getCurrentUser().getEmail();
    private List<Usuario> usuarioList;
    private ContatosAdapter contatosAdapter;
    private RecyclerView rvContatos;
    private final OnItemClickListener<Usuario> itemClickListener = u->{
        Intent i;
        if (u.getEmail().isEmpty()){
            i = new Intent(getActivity(), GrupoActivity.class);
        }else {
            i = new Intent(getActivity(), ChatActivity.class);
            i.putExtra("chatContato", (Serializable) u);
        }
        startActivity(i);
    };
    public ContatosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contatos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        usuarioList = new ArrayList<>();
        contatosAdapter = new ContatosAdapter(getActivity(), usuarioList, itemClickListener);
        rvContatos = view.findViewById(R.id.recycleView_contatos);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvContatos.setLayoutManager(layoutManager);
        rvContatos.setHasFixedSize(true);
        rvContatos.setAdapter(contatosAdapter);
        resetUsuarioList();
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

    private void recuperarContatos(){
        usuarioService.setUsuarioEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                resetUsuarioList();
                for (DataSnapshot dados: snapshot.getChildren()){
                    Usuario usuario = dados.getValue(Usuario.class);
                    if(usuario != null && !UEMAIL.equals(usuario.getEmail())){
                        usuarioList.add(usuario);
                    }
                }
                contatosAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {}
        });
        usuarioService.start();
    }

    private void resetUsuarioList(){
        usuarioList.clear();
        usuarioList.add(0, new Usuario(getString(R.string.new_group), "", null));
    }

    @SuppressLint("NotifyDataSetChanged")
    public void onSearch(String texto){
        List<Usuario> buscaList = new ArrayList<>();

        for (Usuario usuario: usuarioList) {
            String nome = usuario.getNome().toLowerCase(Locale.ROOT);
            String email = usuario.getEmail().toLowerCase(Locale.ROOT);
            if (nome.contains(texto) || email.contains(texto)){
                buscaList.add(usuario);
            }
        }
        contatosAdapter = new ContatosAdapter(getContext(), buscaList, itemClickListener);
        rvContatos.setAdapter(contatosAdapter);
        contatosAdapter.notifyDataSetChanged();

    }

    @SuppressLint("NotifyDataSetChanged")
    public void onClose(){
        contatosAdapter = new ContatosAdapter(getContext(), usuarioList, itemClickListener);
        rvContatos.setAdapter(contatosAdapter);
        contatosAdapter.notifyDataSetChanged();
    }

}