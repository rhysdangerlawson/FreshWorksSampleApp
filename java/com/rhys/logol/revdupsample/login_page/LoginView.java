package com.rhys.logol.revdupsample.login_page;

import com.google.firebase.auth.FirebaseUser;

interface LoginView {
    void showProgress();

    void hideProgress();

    void setUsernameError();

    void setPasswordError();

    void navigateToHome();

    void hideKeyboard();

    void showKeyboard();
}