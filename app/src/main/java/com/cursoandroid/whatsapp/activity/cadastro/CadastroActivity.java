package com.cursoandroid.whatsapp.activity.cadastro;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.cursoandroid.whatsapp.R;
import com.cursoandroid.whatsapp.config.FirebaseConfig;
import com.cursoandroid.whatsapp.model.Usuario;
import com.cursoandroid.whatsapp.helper.Base64Custom;
import com.cursoandroid.whatsapp.serviceImpl.UsuarioService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import java.util.Objects;

public class CadastroActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        EditText campoNome = findViewById(R.id.etCadastrarNome);
        EditText campoEmail = findViewById(R.id.etCadastrarEmail);
        EditText campoSenha = findViewById(R.id.etCadastrarPassword);
        Button btnCadastrar = findViewById(R.id.btnCadastrar);
        CadastroViewModel cadastroViewModel = new ViewModelProvider(this, new CadastroViewModel.CadastroViewModelFactory())
                .get(CadastroViewModel.class);

        cadastroViewModel.getLoginFormState().observe(this, formState -> {
            if (formState == null) {
                return;
            }
            btnCadastrar.setEnabled(formState.isDataValid());
            if (formState.getUsernameError() != null) {
                campoNome.setError(getString(formState.getUsernameError()));
            }
            if (formState.getEmailError() != null) {
                campoEmail.setError(getString(formState.getEmailError()));
            }
            if (formState.getPasswordError() != null) {
                campoSenha.setError(getString(formState.getPasswordError()));
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                cadastroViewModel.loginDataChanged(campoNome.getText().toString(),
                        campoEmail.getText().toString(), campoSenha.getText().toString());
            }
        };
        campoNome.addTextChangedListener(afterTextChangedListener);
        campoEmail.addTextChangedListener(afterTextChangedListener);
        campoSenha.addTextChangedListener(afterTextChangedListener);
        campoSenha.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                cadastrarUsuario(campoNome.getText().toString(), campoEmail.getText().toString(),
                        campoSenha.getText().toString());
            }
            return false;
        });

        btnCadastrar.setOnClickListener(view -> cadastrarUsuario(campoNome.getText().toString(),
                campoEmail.getText().toString(), campoSenha.getText().toString()));
    }

    private void cadastrarUsuario(String username, String email, String password) {
        final UsuarioService usuarioService = new UsuarioService();
        final Usuario usuario = new Usuario(username, email, password);

        FirebaseAuth autenticacao = FirebaseConfig.getFirebaseAuth();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        try {
                            usuario.setId(Base64Custom.encoder(email));
                            usuarioService.save(usuario);
                        }catch (Exception e){
                            e.printStackTrace();
                        }finally {
                            finish();
                        }
                    } else {
                        String excecao;
                        try {
                            throw Objects.requireNonNull(task.getException());
                        } catch (FirebaseAuthWeakPasswordException ignored) {
                            excecao = getString(R.string.invalid_password);
                        } catch (FirebaseAuthInvalidCredentialsException ignored) {
                            excecao = getString(R.string.invalid_credentials);
                        } catch (FirebaseAuthUserCollisionException ignored) {
                            excecao = getString(R.string.invalid_username);
                        } catch (Exception e) {
                            excecao = e.getMessage();
                            e.printStackTrace();
                        }
                        Toast.makeText(this, excecao, Toast.LENGTH_LONG).show();
                    }
                }
        );

    }
}