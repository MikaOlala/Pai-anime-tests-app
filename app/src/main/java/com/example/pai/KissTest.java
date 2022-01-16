package com.example.pai;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ViewUtils;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;

public class KissTest extends AppCompatActivity {

    private ImageView first, second, third, iconOne, iconTwo, iconTh, circleStatOne, circleStatTwo, circleStatTh; // pictures and icons on layout / pictures for statistic
    private Dialog dialog, dialogStat; // pressing picture or call statistic
    private ImageView currentPic; // show pic in dialog
    private String currentPicId, firstPic, secondPic, thirdPic, kiss, marry, kill; // pic names from db and notes for your choices
    private int currentChoiceId, roundId, rowNumber; // act choice / id round from db / id progress
    private MDH myDB;
    private SharedPreferences sharedPreferences;
    private DatabaseReference databaseReference;
    private Statistic currentStatistic;
    private boolean confirmPressed = false;
    private boolean endOfTheTest = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kiss_test);

        sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        // is this a normal quantity of values?
        Button skip = findViewById(R.id.kissSkip);
        Button confirm = findViewById(R.id.kissConfirm);
        skip.setTypeface(ResourcesCompat.getFont(KissTest.this, R.font.gloria_hallelujah));
        confirm.setTypeface(ResourcesCompat.getFont(KissTest.this, R.font.gloria_hallelujah));

        first = findViewById(R.id.one);
        second = findViewById(R.id.two);
        third = findViewById(R.id.three);
        iconOne = findViewById(R.id.oneIcon);
        iconTwo = findViewById(R.id.twoIcon);
        iconTh = findViewById(R.id.threeIcon);

        myDB = new MDH(KissTest.this);
        rowNumber = getProgress();

        resetData();

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(iconOne.getVisibility() == View.VISIBLE &&
                iconTwo.getVisibility() == View.VISIBLE &&
                iconTh.getVisibility() == View.VISIBLE) {
                    if(!kiss.equals(marry) && !kill.equals(marry) && !kill.equals(kiss)) {

                        myDB.editStatistic(String.valueOf(roundId), kiss.substring(0, 3), marry.substring(0, 3), kill.substring(0, 3));
                        confirmPressed = true;
                        goNext();
                        //make dialog statistic
                    }
                    else
                        Toast.makeText(KissTest.this, "Do not repeat", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(KissTest.this, "You forgot someone", Toast.LENGTH_SHORT).show();
            }
        });
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!endOfTheTest)
                    goNext();
                else
                    openActivity(); // there is a little problem bc of the methods order. If you skip the last round, you need to press button twice
            }
        });
    }

    public void resetData() {
        try {
            loadData(rowNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }
        loadImage(firstPic, first);
        loadImage(secondPic, second);
        loadImage(thirdPic, third);
        myDB.existOrCreate(String.valueOf(roundId));
        confirmPressed = false;
        loadPieChartData();
    }
    public void clearData() {
        iconOne.setVisibility(View.INVISIBLE);
        iconTwo.setVisibility(View.INVISIBLE);
        iconTh.setVisibility(View.INVISIBLE);
        kiss = null;
        marry = null;
        kill = null;
        first.clearColorFilter();
        second.clearColorFilter();
        third.clearColorFilter();

        if (confirmPressed)
            openDialogStat();

        resetData();
    }

    public void picClick(View view) {
        currentPic = findViewById(view.getId());
        String number = getResources().getResourceEntryName(view.getId());
        currentPicId = number + "Icon";

        dialog = new Dialog(KissTest.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_frame);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ImageView image = dialog.findViewById(R.id.image);
        TextView textName = dialog.findViewById(R.id.textName);
        TextView textAnime = dialog.findViewById(R.id.textAnime);
        Character character = null;

        switch (number) {
            case "one":
                character = loadCharacter(firstPic);
                loadImage(firstPic, image);
                break;
            case "two":
                character = loadCharacter(secondPic);
//                image.setImageDrawable(second.getDrawable());  // this kakashka remove rounded angles on my picture >:C
                loadImage(secondPic, image);
                break;
            case "three":
                character = loadCharacter(thirdPic);
                loadImage(thirdPic, image);
                break;
        }

        textName.setText(character.getName());
        textAnime.setText(character.getAnime());
        dialog.show();
    }

    public void buttonClick(View view) {
        int idNumber = view.getId();
        String choice = getResources().getResourceEntryName(idNumber);
        switch (choice) {
            case "buttonKiss":
                kiss = currentPicId;
                currentChoiceId = 1;
                break;
            case "buttonMarry":
                marry = currentPicId;
                currentChoiceId = 2;
                break;
            case "buttonKill":
                kill = currentPicId;
                currentChoiceId = 3;
                break;
        }
        dialog.cancel();
        doTransparent();
    }

    public void doTransparent () {
        currentPic.setColorFilter(Color.rgb(123, 123, 123), android.graphics.PorterDuff.Mode.MULTIPLY);

        int id = getResources().getIdentifier(currentPicId, "id", KissTest.this.getPackageName());
        ImageView icon = findViewById(id);
        switch (currentChoiceId) {
            case 1:
                icon.setImageResource(R.drawable.lips3);
                break;
            case 2:
                icon.setImageResource(R.drawable.ring3);
                break;
            case 3:
                icon.setImageResource(R.drawable.skull3);
                break;
        }
        icon.setVisibility(View.VISIBLE);
        Log.d("AAAAAAA", "pic name: " + icon.getDrawable());
    }

    public void layoutClick(View view) {
        dialog.cancel();
    }

    public void loadData(int rowNumber) throws IOException {
        Cursor cursor = myDB.getRound(rowNumber);
        if(cursor.moveToFirst()){
            roundId = cursor.getInt(0);
            firstPic = cursor.getString(1);
            secondPic = cursor.getString(2);
            thirdPic = cursor.getString(3);

            cursor.close();
        }
        else {
            endOfTheTest = true;
        }
    }

    public void loadImage(String imageName, ImageView imageView) {
        databaseReference = FirebaseDatabase.getInstance("https://paiimages-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference().child("imagesKiss");
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

    public Character loadCharacter(String imageName) {
        Character character = null;
        Cursor cursor = myDB.getCharacter(imageName);
        if(cursor.moveToFirst()){
            character = new Character(cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2));
            cursor.close();
        }
        return character;
    }

    public void goNext() {
        rowNumber = rowNumber + 1;
        setProgress("kissTestProgress", String.valueOf(rowNumber));
        clearData();
    }

    public void openDialogStat() {
        dialogStat = new Dialog(KissTest.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialogStat.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogStat.setContentView(R.layout.dialog_stat);

        circleStatOne = dialogStat.findViewById(R.id.circleStatOne);
        circleStatTwo = dialogStat.findViewById(R.id.circleStatTwo);
        circleStatTh = dialogStat.findViewById(R.id.circleStatTh);
        TextView voters = dialogStat.findViewById(R.id.voteText);
        RelativeLayout cancel = dialogStat.findViewById(R.id.cancelStat);
        PieChart firstCh = dialogStat.findViewById(R.id.firstCharacter);
        PieChart secondCh = dialogStat.findViewById(R.id.secondCharacter);
        PieChart thirdCh = dialogStat.findViewById(R.id.thirdCharacter);

        // not so elegant code ;;
        ArrayList<PieEntry> statForFirst = new ArrayList<>();
        statForFirst.add(new PieEntry(getPercent(currentStatistic.getTotal(), currentStatistic.getKiss_one()), "Kiss"));
        statForFirst.add(new PieEntry(getPercent(currentStatistic.getTotal(), currentStatistic.getMarry_one()), "Marry"));
        statForFirst.add(new PieEntry(getPercent(currentStatistic.getTotal(), currentStatistic.getKill_one()), "Kill"));

        ArrayList<PieEntry> statForSecond = new ArrayList<>();
        statForSecond.add(new PieEntry(getPercent(currentStatistic.getTotal(), currentStatistic.getKiss_two()), "Kiss"));
        statForSecond.add(new PieEntry(getPercent(currentStatistic.getTotal(), currentStatistic.getMarry_two()), "Marry"));
        statForSecond.add(new PieEntry(getPercent(currentStatistic.getTotal(), currentStatistic.getKill_two()), "Kill"));

        ArrayList<PieEntry> statForThird = new ArrayList<>();
        statForThird.add(new PieEntry(getPercent(currentStatistic.getTotal(), currentStatistic.getKiss_th()), "Kiss"));
        statForThird.add(new PieEntry(getPercent(currentStatistic.getTotal(), currentStatistic.getMarry_th()), "Marry"));
        statForThird.add(new PieEntry(getPercent(currentStatistic.getTotal(), currentStatistic.getKill_th()), "Kill"));

        setNewChart(firstCh, statForFirst, "first");
        setNewChart(secondCh, statForSecond, "second");
        setNewChart(thirdCh, statForThird, "third");

        // I don't want to use this method everytime
        loadImage(firstPic, circleStatOne);
        loadImage(secondPic, circleStatTwo);
        loadImage(thirdPic, circleStatTh);
        voters.setText("Total Voters: " + currentStatistic.getTotal());
        dialogStat.show();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!endOfTheTest)
                    dialogStat.cancel();
                else {
                    dialogStat.cancel();
                    openActivity();
                }
            }
        });
    }

    private float getPercent (int total, int act) {
        return (float) act * 100 / total;
    }

    private void setNewChart(PieChart chart, ArrayList<PieEntry> entries, String name) {
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setEntryLabelTextSize(12);
        chart.setEntryLabelColor(R.color.darkViola);
        chart.setUsePercentValues(true);
        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(getResources().getColor(R.color.ghost));

        PieDataSet dataSet = new PieDataSet(entries, name);
        dataSet.setColors(getResources().getColor(R.color.pinkySarah), getResources().getColor(R.color.lightViola), getResources().getColor(R.color.eyeKiller));

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(chart));
        data.setValueTextSize(12);
        data.setValueTextColor(R.color.darkViola);

        chart.setData(data);
        chart.invalidate();

        chart.animateY(1400, Easing.EaseInOutQuad);
    }

    private void loadPieChartData() {
        databaseReference = FirebaseDatabase.getInstance("https://paiimages-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference().child("statistic").child(String.valueOf(rowNumber));
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentStatistic = snapshot.getValue(Statistic.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("loadPieChartData", error.getMessage());
            }
        });
    }

    public int getProgress() {
        int rowNumber = -1;
        String progress = sharedPreferences.getString("kissTestProgress", null);
        if(progress!=null) {
            rowNumber = Integer.parseInt(progress);
        }
        else
            Log.d("AAAAAAAAAAA", "Can't identify progress");
        return rowNumber;
    }

    public void setProgress(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void openActivity() {
        Intent intent = new Intent(this, AnimeTestResult.class);
        intent.putExtra("test", "kissTest");
        startActivity(intent);
    }

}