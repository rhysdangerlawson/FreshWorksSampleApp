package com.rhys.logol.revdupsample.login_page;

class LoginPresenterImpl implements LoginPresenter, LoginInteractor.OnLoginFinishedListener {

    private static final String TAG = "LoginPresenterImpl";

    private LoginView loginView;
    private LoginInteractor loginInteractor;

    LoginPresenterImpl(LoginView loginView) {
        this.loginView = loginView;
        this.loginInteractor = new LoginInteractorImpl();
    }

    @Override public void validateCredentials(String username, String password) {
        loginInteractor.login(username, password, this);
    }

    @Override public void onDestroy() {
        loginView = null;
    }

    @Override public void onUsernameError() {
        if (loginView != null) {
            loginView.setUsernameError();
            loginView.hideProgress();
        }
    }

    @Override public void onPasswordError() {
        if (loginView != null) {
            loginView.setPasswordError();
            loginView.hideProgress();
        }
    }

    @Override public void onSuccess() {
        if (loginView != null) {
            loginView.navigateToHome();
        }
    }
}