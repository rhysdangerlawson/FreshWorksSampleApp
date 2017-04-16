package com.rhys.logol.revdupsample.main_page.Feed;

import android.net.Uri;

interface ShowImagesPresenter {
    String getFileExtension(Uri uri);

    void setFilePath(Uri path);

    Uri getFilePath();
}
