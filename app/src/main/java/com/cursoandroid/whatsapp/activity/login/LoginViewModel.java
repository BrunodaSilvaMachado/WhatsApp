package com.cursoandroid.whatsapp.activity.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.util.Patterns;

import com.cursoandroid.whatsapp.R;
import com.cursoandroid.whatsapp.activity.FormState;

public class LoginViewModel extends ViewModel {

    private final MutableLiveData<FormState> loginFormState = new MutableLiveData<>();

    LoginViewModel() {
    }

    LiveData<FormState> getLoginFormState() {
        return loginFormState;
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new FormState(R.string.invalid_username,null, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new FormState(null, null,  R.string.invalid_password));
        } else {
            loginFormState.setValue(new FormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}