package com.cursoandroid.whatsapp.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.cursoandroid.whatsapp.R;
import com.cursoandroid.whatsapp.activity.MainActivity;
import com.cursoandroid.whatsapp.activity.cadastro.CadastroActivity;
import com.cursoandroid.whatsapp.config.FirebaseConfig;
import com.cursoandroid.whatsapp.model.Usuario;
import com.cursoandroid.whatsapp.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private final FirebaseAuth auth = FirebaseConfig.getFirebaseAuth();
    private LoginViewModel loginViewModel;
    private ProgressBar loadingProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;
        loadingProgressBar = binding.loading;
        final TextView newUser = binding.newUser;

        loginViewModel.getLoginFormState().observe(this, loginFormState -> {
            if (loginFormState == null) {
                return;
            }
            loginButton.setEnabled(loginFormState.isDataValid());
            if (loginFormState.getUsernameError() != null) {
                usernameEditText.setError(getString(loginFormState.getUsernameError()));
            }
            if (loginFormState.getPasswordError() != null) {
                passwordEditText.setError(getString(loginFormState.getPasswordError()));
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
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                login(usernameEditText.getText().toString(), passwordEditText.getText().toString());
            }
            return false;
        });

        loginButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            login(usernameEditText.getText().toString(), passwordEditText.getText().toString());
        });

        newUser.setOnClickListener(v->startActivity(new Intent(LoginActivity.this, CadastroActivity.class)));
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            updateUiWithUser();
        }
    }

    private void updateUiWithUser() {
        startActivity(new Intent(this, MainActivity.class));
    }

    private void showLoginFailed(String errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    private void login(String username, String password) {
        Usuario usuario = new Usuario(null, username, password);
        auth.signInWithEmailAndPassword(usuario.getEmail(), usuario.getSenha())
                .addOnCompleteListener(task->
                {
                    loadingProgressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()){
                        updateUiWithUser();
                    }else {
                        String excecao;
                        try {
                            throw Objects.requireNonNull(task.getException());
                        }catch (FirebaseAuthInvalidCredentialsException ignored){
                            excecao = getString(R.string.invalid_credentials);
                        }catch (FirebaseAuthInvalidUserException ignored){
                            excecao = getString(R.string.invalid_username);
                        }catch (Exception e){
                            excecao = getString(R.string.login_failed) + ": " + e.getMessage();
                            e.printStackTrace();
                        }
                        showLoginFailed(excecao);
                    }
                });
    }
}