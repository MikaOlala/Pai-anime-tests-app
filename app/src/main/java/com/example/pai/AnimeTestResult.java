package com.example.pai;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class AnimeTestResult extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anime_test_result);

        TextView title = findViewById(R.id.title);
        TextView textView = findViewById(R.id.scoreText);
        Button back = findViewById(R.id.buttonBack);
        back.setTypeface(ResourcesCompat.getFont(AnimeTestResult.this, R.font.gloria_hallelujah));

        Bundle arguments = getIntent().getExtras();
        String whichTest = (String) arguments.get("test");
        if(whichTest.equals("animeTest")) {
            String score = (String) arguments.get("result");
            String total = (String) arguments.get("total");

            textView.setText(score + "/" + total);
        }
        else {
            title.setText("Nice one!");
            title.setTextSize(50);
            textView.setText("You've passed all rounds. Please, wait for new updates.");
            textView.setTextSize(15);
        }


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity();
            }
        });
    }

    public void openActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}