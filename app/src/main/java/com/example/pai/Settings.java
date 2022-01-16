package com.example.pai;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        RelativeLayout gear = findViewById(R.id.addAnimeTest);
        RelativeLayout gearToKiss = findViewById(R.id.addKissTest);
        RelativeLayout back = findViewById(R.id.back_button);

        Button resetAnimeTest = findViewById(R.id.resetAnimeTest);
        Button resetKissTest = findViewById(R.id.resetKissTest);

        resetAnimeTest.setTypeface(ResourcesCompat.getFont(Settings.this, R.font.gloria_hallelujah));
        resetKissTest.setTypeface(ResourcesCompat.getFont(Settings.this, R.font.gloria_hallelujah));

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity();
            }
        });
        gear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDataControl();
            }
        });
        gearToKiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDataKissControl();
            }
        });
        resetAnimeTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "Anime test was reseted", Toast.LENGTH_SHORT).show();
                add("animeTestProgress", "1");
                add("animeTestScore", "0");
            }
        });
        resetKissTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Settings.this, "Kiss/Marry/Kill was reseted", Toast.LENGTH_SHORT).show();
                add("kissTestProgress", "1");
            }
        });
    }

    private void startActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    private void startDataControl() {
        Intent intent = new Intent(this, DataControl.class);
        startActivity(intent);
    }
    private void startDataKissControl() {
        Intent intent = new Intent(this, DataKissControl.class);
        startActivity(intent);
    }

    private void add(String task, String value) {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(task, value);
        editor.apply();
    }
}