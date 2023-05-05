package com.cursoandroid.whatsapp.fragment;

import android.annotation.SuppressLint;
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
import com.cursoandroid.whatsapp.adapter.ConversasAdapter;
import com.cursoandroid.whatsapp.model.Conversa;
import com.cursoandroid.whatsapp.serviceImpl.ConversaService;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConversasFragment extends Fragment implements SearchArrayList {

    private final ConversaService conversaService = new ConversaService();
    private List<Conversa> conversaList;
    private ConversasAdapter conversasAdapter;
    private RecyclerView rvConversas;
    public ConversasFragment() {
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
        return inflater.inflate(R.layout.fragment_conversas, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        conversaList = new ArrayList<>();
        conversasAdapter = new ConversasAdapter(getContext(), conversaList);
        rvConversas = view.findViewById(R.id.recycleView_conversas);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvConversas.setLayoutManager(layoutManager);
        rvConversas.setHasFixedSize(true);
        rvConversas.setAdapter(conversasAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarConversas();
    }

    @Override
    public void onStop() {
        super.onStop();
        conversaService.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        conversaService.finish();
    }

    private void recuperarConversas(){
        conversaList.clear();
        conversaService.setEventListener(new ChildEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                Conversa conversa = snapshot.getValue(Conversa.class);
                conversaList.add(conversa);
                conversasAdapter.notifyDataSetChanged();
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
        conversaService.start();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void onSearch(String texto){
        List<Conversa> buscaList = new ArrayList<>();

        for (Conversa conversa: conversaList) {
            String nome = (conversa.isGrupoConversa())? conversa.getGrupo().getNome().toLowerCase(Locale.ROOT):
                    conversa.getUsuarioExibicao().getNome().toLowerCase(Locale.ROOT);
            String ultimaMsg = conversa.getUltimaMensagem().toLowerCase(Locale.ROOT);
            if (nome.contains(texto) || ultimaMsg.contains(texto)){
                buscaList.add(conversa);
            }
        }
        conversasAdapter = new ConversasAdapter(getContext(), buscaList);
        rvConversas.setAdapter(conversasAdapter);
        conversasAdapter.notifyDataSetChanged();

    }

    @SuppressLint("NotifyDataSetChanged")
    public void onClose(){
        conversasAdapter = new ConversasAdapter(getContext(), conversaList);
        rvConversas.setAdapter(conversasAdapter);
        conversasAdapter.notifyDataSetChanged();
    }


}