package com.rhys.logol.revdupsample.main_page.Feed;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

class ShowImagesInteractorImpl implements ShowImagesInteractor{
    private static FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private static final String STORAGE_PATH_UPLOADS = "users/" + user.getUid() + "/";
    private static final String DATABASE_PATH_UPLOADS = "users/" + user.getUid() + "/" + "Posts";

    ShowImagesInteractorImpl(){

    }

    @Override public String getSTORAGE_PATH_UPLOADS() {
        return STORAGE_PATH_UPLOADS;
    }

    @Override public String getDATABASE_PATH_UPLOADS() {
        return DATABASE_PATH_UPLOADS;
    }
}
