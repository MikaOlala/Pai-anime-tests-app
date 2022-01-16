package com.example.pai;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    SharedPreferences prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("com.example.pai", MODE_PRIVATE);
        View guess = findViewById(R.id.menuGuess);
        View kiss = findViewById(R.id.menuKiss);
        View maker = findViewById(R.id.menuMaker);
        View gear = findViewById(R.id.gear);
        guess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivityGuess();
            }
        });
        kiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivityKiss();
            }
        });
        maker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivityMaker();
            }
        });
        gear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivitySettings();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(prefs.getBoolean("firstrun", true)) {
            Log.d("AAAAAAAAAAAAAAAAAA", "it works");
            SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("user name", "User1234");
            editor.putString("user gender", "Unchecked");
            editor.putString("animeTestProgress", "1");
            editor.putString("animeTestScore", "0");
            editor.putString("kissTestProgress", "1");
            editor.apply();

            prefs.edit().putBoolean("firstrun", false).apply();
        }
    }

    public void openActivityGuess() {
        Intent intent = new Intent(this, AnimeTest.class);
        startActivity(intent);
    }
    public void openActivityKiss() {
        Intent intent = new Intent(this, KissTest.class);
        startActivity(intent);
    }
    public void openActivityMaker() {
        Intent intent = new Intent(this, AvatarMaker.class);
        startActivity(intent);
    }
    public void openActivitySettings() {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }
    
}