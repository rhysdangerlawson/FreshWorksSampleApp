package com.rhys.logol.revdupsample.main_page.Feed;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rhys.logol.revdupsample.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

public class ShowImagesActivity extends Fragment implements View.OnClickListener,ShowImagesView {

    //The lone dialog
    private ProgressDialog progressDialog;

    //Feed variables
    private List<Post> posts;
    Uri filePath;
    private String uploadId;
    private Boolean tog = true;

    //Recycling
    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;

    //Server stuff
    private StorageReference storageReference;
    private DatabaseReference mDatabase;

    //constant to track image chooser intent
    private static final int PICK_IMAGE_REQUEST = 1;

    private FloatingActionButton fab;

    //Dialogs
    private Dialog postDialog;
    private ImageButton addImage;
    public EditText description;

    private ShowImagesPresenter presenter;
    private ShowImagesInteractor interactor;

    @Nullable
    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                       @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_show_images, container, false);
        rootView.setTag(TAG);

        // Setting up recyclerView
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Set up the presenter and interactor
        presenter = new ShowImagesPresenterImpl(this, new ShowImagesInteractorImpl());
        interactor = new ShowImagesInteractorImpl();

        final ProgressDialog progressDialog = new ProgressDialog(getContext());

        // This will essentially be our feed
        posts = new ArrayList<>();

        // Displaying progress dialog while fetching images
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        // Initializing our database and storage references
        mDatabase = FirebaseDatabase.getInstance().getReference(interactor.getDATABASE_PATH_UPLOADS());
        storageReference = FirebaseStorage.getInstance().getReference();

        // When in doubt, be fab :D
        fab = (FloatingActionButton) rootView.findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(this);

        postDialog = new Dialog(getContext());

        //adding an event listener to fetch values
        mDatabase.addValueEventListener(new ValueEventListener() {

            @Override public void onDataChange(DataSnapshot snapshot) {
                //dismissing the progress dialog
                progressDialog.dismiss();

                //TODO: Find better way to structure feed without using reverse().
                if(tog) {
                    //////////////////////////////////////////////////////////////////
                    //This section is for pulling the entire database onto the feed.//
                    //////////////////////////////////////////////////////////////////

                    //iterating through all the values in database
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        Post post = postSnapshot.getValue(Post.class);
                        posts.add(post);
                    }
                    //Display most recent post first
                    Collections.reverse(posts);
                }else{
                    ///////////////////////////////////////////////////////////////////
                    //This section is for pulling the most recent post onto the feed.//
                    ///////////////////////////////////////////////////////////////////

                    //Flip back to normal, add to array
                    Collections.reverse(posts);
                    Post post = snapshot.child(uploadId).getValue(Post.class);
                    posts.add(post);
                    //Flip back to most recent post first
                    Collections.reverse(posts);
                }

                // Takes care of duplicate feed bug
                tog = false;

                // Creating adapter
                adapter = new MyAdapter(getContext(), posts);

                // Adding adapter to recyclerView
                recyclerView.setAdapter(adapter);
            }

            @Override public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });

        return rootView;
    }

    // Allows image to be selected from gallery.
    @Override public void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    // Dialog that pops up to post.
    @Override public void showPostDialog(){
        postDialog.setContentView(R.layout.post_dialog);
        postDialog.setTitle("Post Dialog");

        postDialog.show();

        Button submit = (Button) postDialog.findViewById(R.id.post_button);
        addImage = (ImageButton) postDialog.findViewById(R.id.add_image);
        description = (EditText) postDialog.findViewById(R.id.description);

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postDialog.dismiss();
                uploadFile();
            }
        });
    }

    // Handles selected image data.
    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            presenter.setFilePath(filePath = data.getData());
            Glide.with(this).load(filePath).centerCrop().into(addImage);
        }
    }

    // OnClick for fab.
    @Override public void onClick(View view) {
        if(view == fab){
            showPostDialog();
        }
    }

    //Uploads file to Firebase storage
    @Override public void uploadFile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //checking if file is available
        if (filePath != null) {
            //displaying progress dialog while image is uploading
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading");
            progressDialog.show();


            // Initialize the storage reference
            StorageReference sRef = storageReference.child("users/" + user.getUid() +
                    "/" + System.currentTimeMillis() + "." + presenter.getFileExtension(filePath));

            // Adding the file to reference
            sRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //dismissing the progress dialog
                            progressDialog.dismiss();

                            //displaying success toast
                            Toast.makeText(getContext(), "File Uploaded ", Toast.LENGTH_LONG).show();

                            //creating the upload object to store uploaded image details
                            Post post = new Post(description.getText().toString().trim(), taskSnapshot.getDownloadUrl().toString());

                            //adding an upload to firebase database
                            uploadId = mDatabase.push().getKey();
                            mDatabase.child(uploadId).setValue(post);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //displaying the upload progress
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        } else {
            Toast.makeText(getContext(), "No image was selected.", Toast.LENGTH_SHORT).show();
        }
    }

    // Experimenting with fragments
    public static ShowImagesActivity newInstance(String text) {

        ShowImagesActivity f = new ShowImagesActivity();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);
        return f;
    }
}