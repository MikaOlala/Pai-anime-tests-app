package com.example.pai;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class DataKissControl extends AppCompatActivity {

    private EditText nameF, animeF, nameS, animeS, nameT, animeT;
    private Button add;
    private Uri imageUri;
    private ImageView picFirst, picSecond, picThird;
    private String currentImage, nameFirst, nameSecond, nameThird;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_kiss_control);

        picFirst = findViewById(R.id.picFirst);
        picSecond = findViewById(R.id.picSecond);
        picThird = findViewById(R.id.picThird);
        add = findViewById(R.id.addButton);
        nameF = findViewById(R.id.nameFirst);
        nameS = findViewById(R.id.nameSecond);
        nameT = findViewById(R.id.nameThird);
        animeF = findViewById(R.id.animeFirst);
        animeS = findViewById(R.id.animeSecond);
        animeT = findViewById(R.id.animeThird);

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance("https://paiimages-default-rtdb.europe-west1.firebasedatabase.app").getReference("imagesKiss");

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MDH myDB = new MDH(DataKissControl.this);
                //make statement for null data
                myDB.addRound(nameFirst, nameSecond, nameThird);
                myDB.addCharacter(nameFirst, nameF.getText().toString().trim(), animeF.getText().toString().trim());
                myDB.addCharacter(nameSecond, nameS.getText().toString().trim(), animeS.getText().toString().trim());
                myDB.addCharacter(nameThird, nameT.getText().toString().trim(), animeT.getText().toString().trim());

                Toast.makeText(DataKissControl.this, "New Round Added", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void choosePicture(View view) {
        int idNumber = view.getId();
        currentImage = getResources().getResourceEntryName(idNumber);

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null) {
            imageUri = data.getData();
            switch (currentImage) {
                case "picFirst":
                    picFirst.setImageURI(imageUri);
                    break;
                case "picSecond":
                    picSecond.setImageURI(imageUri);
                    break;
                case "picThird":
                    picThird.setImageURI(imageUri);
                    break;
            }
            uploadPicture();
        }
    }

    private void rememberName(String name) {
        switch (currentImage) {
            case "picFirst":
                nameFirst = name;
                break;
            case "picSecond":
                nameSecond = name;
                break;
            case "picThird":
                nameThird = name;
                break;
        }
    }

    private void uploadPicture() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading Image...");
        pd.show();

        final String randomKey = UUID.randomUUID().toString();
        rememberName(randomKey);
        StorageReference riversRef = storageReference.child("imagesKiss/" + randomKey);

        riversRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(), "Image uploaded", Toast.LENGTH_SHORT).show();

                        if(taskSnapshot.getMetadata().getReference() != null) {
                            Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Upload upload = new Upload(randomKey, uri.toString());
                                    String uploadId = databaseReference.push().getKey();
                                    databaseReference.child(uploadId).setValue(upload);
                                }
                            });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(), "Failed to upload", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        double progressPercent = (100.00 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        pd.setMessage("Percentage: " + (int) progressPercent + "%");
                    }
                });
    }
}