package com.rhys.logol.revdupsample.main_page.Feed;

import android.content.ContentResolver;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

class ShowImagesPresenterImpl implements ShowImagesPresenter{
    private String uploadId;

    private Uri filePath;

    private EditText description;

    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    private ShowImagesActivity showImagesActivity;
    private ShowImagesInteractor showImagesInteractor;

    ShowImagesPresenterImpl(ShowImagesActivity showImagesActivity, ShowImagesInteractor showImagesInteractor){
        this.showImagesActivity = showImagesActivity;
        this.showImagesInteractor = showImagesInteractor;
    }

    //Obtains whether file is .JPEG/.PNG/.GIF
    @Override public String getFileExtension(Uri uri) {
        ContentResolver cR = showImagesActivity.getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    @Override public void setFilePath(Uri path){
        this.filePath = path;
    }

    @Override public Uri getFilePath(){
        return filePath;
    }
}
