package com.example.pai;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

public class AnimeTest extends AppCompatActivity {

    private ImageView imageView, skip;
    private TextView textSkip;
    private Button first, second, third, fourth, pressed, rightButton;
    private String answer, imageName, imageExt;
    private DatabaseReference databaseReference;
    private int rowNumber;
    private SharedPreferences sharedPreferences;

    private MDH myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anime_test);

        sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        imageView = findViewById(R.id.picTest);
        skip = findViewById(R.id.skip_test);
        first = findViewById(R.id.button_test_first);
        second = findViewById(R.id.button_test_second);
        third = findViewById(R.id.button_test_third);
        fourth = findViewById(R.id.button_test_fourth);
        textSkip = findViewById(R.id.textSkip);

        first.setTypeface(ResourcesCompat.getFont(AnimeTest.this, R.font.gloria_hallelujah));
        second.setTypeface(ResourcesCompat.getFont(AnimeTest.this, R.font.gloria_hallelujah));
        third.setTypeface(ResourcesCompat.getFont(AnimeTest.this, R.font.gloria_hallelujah));
        fourth.setTypeface(ResourcesCompat.getFont(AnimeTest.this, R.font.gloria_hallelujah));

        myDB = new MDH(AnimeTest.this);
        rowNumber = getProgress();

        try {
            loadData(rowNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }

        loadImage();

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newNumber = rowNumber + 1;
                rowNumber = newNumber;

                setProgress("animeTestProgress", String.valueOf(newNumber));
                try {
                    loadData(rowNumber);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                loadImage();

                first.setEnabled(true);
                second.setEnabled(true);
                third.setEnabled(true);
                fourth.setEnabled(true);
                textSkip.setText("Skip");
                if (pressed!=null) {
                    pressed.setBackground(getResources().getDrawable(R.drawable.button_shadow));
                    rightButton.setBackground(getResources().getDrawable(R.drawable.button_shadow));
                }
            }
        });
    }

    public void openActivity() {
        Intent intent = new Intent(this, AnimeTestResult.class);
        intent.putExtra("test", "animeTest");
        intent.putExtra("result", String.valueOf(getScore()));
        intent.putExtra("total", String.valueOf(rowNumber-1));
        startActivity(intent);
    }

    public void choice(View view) {
        Drawable right = getResources().getDrawable(R.drawable.button_shadow_right);
        pressed = (Button) view;
        String title = pressed.getText().toString();
        if(title.equals(answer)) {
            pressed.setBackground(right);
            rightButton = pressed;

            setProgress("animeTestScore", String.valueOf(getScore() + 1));
        }
        else {
            Drawable wrong = getResources().getDrawable(R.drawable.button_shadow_wrong);
            pressed.setBackground(wrong);
            findRightButton();
            rightButton.setBackground(right);
        }
        first.setEnabled(false);
        second.setEnabled(false);
        third.setEnabled(false);
        fourth.setEnabled(false);
        textSkip.setText("Next");
        skip.setImageResource(R.drawable.next_button);
    }

    public void findRightButton() {
        if(answer.equals(first.getText().toString()))
            rightButton = first;
        else if(answer.equals(second.getText().toString()))
            rightButton = second;
        else if(answer.equals(third.getText().toString()))
            rightButton = third;
        else
            rightButton = fourth;
    }

    public int getProgress() {
        int rowNumber = -1;
        String progress = sharedPreferences.getString("animeTestProgress", null);
        if(progress!=null) {
            rowNumber = Integer.parseInt(progress);
        }
        else
            Log.d("AAAAAAAAAAA", "Can't identify progress");
        return rowNumber;
    }

    public int getScore() {
        String score = sharedPreferences.getString("animeTestScore", null);
        return Integer.parseInt(score);
    }

    public void setProgress(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void loadData(int rowNumber) throws IOException {
//        myDB.createDataBase();
//        myDB.openDataBase();

        Cursor cursor = myDB.getQuestion(rowNumber);
        if(cursor.moveToFirst()){
            first.setText(cursor.getString(1));
            second.setText(cursor.getString(2));
            third.setText(cursor.getString(3));
            fourth.setText(cursor.getString(4));

            int correctId = Integer.parseInt(cursor.getString(5));
            answer = cursor.getString(correctId);
            imageName = cursor.getString(6);
            imageExt = cursor.getString(7);

            cursor.close();
//            myDB.close();
        }
        else {
            openActivity();
        }
    }

    public void loadImage() {
        databaseReference = FirebaseDatabase.getInstance("https://paiimages-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference().child("images");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = "";
                for(DataSnapshot ds : snapshot.getChildren()){
                    if(ds.child("name").getValue().toString().equals(imageName)) {
                        value = ds.child("imageUrl").getValue().toString();
                        break;
                    }

                }
                Picasso.get().load(Uri.parse(value)).into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}