package com.rhys.logol.revdupsample.login_page;

interface LoginPresenter {
    void validateCredentials(String email, String password);

    void onDestroy();
}
