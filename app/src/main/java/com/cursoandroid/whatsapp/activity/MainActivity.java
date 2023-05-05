package com.cursoandroid.whatsapp.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import com.cursoandroid.whatsapp.R;
import com.cursoandroid.whatsapp.config.FirebaseConfig;
import com.cursoandroid.whatsapp.fragment.ContatosFragment;
import com.cursoandroid.whatsapp.fragment.ConversasFragment;
import com.cursoandroid.whatsapp.fragment.SearchArrayList;
import com.google.firebase.auth.FirebaseAuth;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private final FirebaseAuth auth = FirebaseConfig.getFirebaseAuth();
    private MaterialSearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
        //Configurar abas
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(getSupportFragmentManager(),
                FragmentPagerItems.with(this)
                        .add(R.string.chat, ConversasFragment.class)
                        .add(R.string.contacts, ContatosFragment.class)
                        .create());
        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        SmartTabLayout tabLayout = findViewById(R.id.viewpagertab);
        tabLayout.setViewPager(viewPager);

        searchView = findViewById(R.id.search_view_principal);
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {}

            @Override
            public void onSearchViewClosed() {
                SearchArrayList searchFragment = (SearchArrayList) adapter.getPage(viewPager.getCurrentItem());
                searchFragment.onClose();
            }
        });
        // Caixa de texto
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                SearchArrayList searchFragment = (SearchArrayList) adapter.getPage(viewPager.getCurrentItem());
                if (newText != null && !newText.isEmpty()) {
                    searchFragment.onSearch(newText.toLowerCase(Locale.ROOT));
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.item_action_search);
        searchView.setMenuItem(item);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_action_search:
                break;
            case R.id.item_action_settings:
                abrirConfiguracoes();
                break;
            case R.id.item_action_quit:
                desconectar();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }

    private void desconectar(){
        try{
            auth.signOut();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void abrirConfiguracoes(){
        startActivity(new Intent(MainActivity.this, ConfiguracoesActivity.class));
    }
}