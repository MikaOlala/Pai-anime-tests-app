package com.example.pai;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.UUID;

public class AvatarMaker extends AppCompatActivity {

    private RadioGridGroup groupSkin;
    private RadioButton skinRadio;
    private ImageView skin, eyes, brows, mouth, hair, back, currentSectionPic;
    private ScrollView currentSection;
    private RelativeLayout save;

    private String eyesId, eyeColorId, hairId, hairColorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar_maker);

        currentSection = findViewById(R.id.skinViewSection);
        currentSectionPic = findViewById(R.id.skinView);
        eyesId = "1";
        eyeColorId = "1";
        hairId = "3";
        hairColorId = "1";

        skin = findViewById(R.id.skin);
        eyes = findViewById(R.id.eyes);
        brows = findViewById(R.id.brows);
        mouth = findViewById(R.id.mouth);
        hair = findViewById(R.id.hair);
        back = findViewById(R.id.back);
        save = findViewById(R.id.save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

    }
    public void chooseItem(View view) {
        String name = getResources().getResourceEntryName(view.getId());
        String elementName = whatIsIt(name);

        int id = AvatarMaker.this.getResources().getIdentifier(elementName, "drawable", AvatarMaker.this.getPackageName());
        switch (elementName.substring(0, 4)){
            case "skin":
                skin.setImageResource(id);
                break;
            case "eyes":
                eyes.setImageResource(id);
                break;
            case "brow":
                brows.setImageResource(id);
                break;
            case "mout":
                mouth.setImageResource(id);
                break;
            case "hair":
                hair.setImageResource(id);
                break;
            case "back":
                back.setImageResource(id);
                break;
        }

    }

    private String whatIsIt(String view) {
        int count = 0;
        for(int i=0; i < view.length(); i++)
        {    if(view.charAt(i) == '_')
                count++;
        }
        if(count == 1)
            return view;
        else {
            if(view.startsWith("color")) {  // changing color
                String id = view.substring(view.length() - 1);
                if (view.startsWith("eye", 6)) {
                    eyeColorId = id;
                    return "eyes_" + eyesId + "_" + id;
                }
                else {
                    hairColorId = id;
                    return "hair_" + hairId + "_" + id;
                }
            }
            else {
                String id = String.valueOf(view.charAt(5));
                if(view.startsWith("eyes")) {
                    eyesId = id;
                    return "eyes_" + id + "_" + eyeColorId;
                }
                else {
                    hairId = id;
                    return "hair_" + id + "_" + hairColorId;
                }
            }
        }
    }

    public void chooseSection(View view) {
        currentSectionPic.setBackgroundResource(R.color.middleTom);
        view.setBackgroundResource(R.color.backSarah);
        String name = getResources().getResourceEntryName(view.getId()) + "Section";

        int id = getResources().getIdentifier(name, "id", AvatarMaker.this.getPackageName());
        ScrollView section = findViewById(id);
        currentSection.setVisibility(View.INVISIBLE);
        section.setVisibility(View.VISIBLE);
        currentSection = section;
        currentSectionPic = (ImageView) view;
    }


    public Bitmap save() {
        save.setVisibility(View.INVISIBLE);
        RelativeLayout memecontentView =(RelativeLayout) findViewById(R.id.avatar);
        View view1 = memecontentView;

        Bitmap b = Bitmap.createBitmap(view1.getWidth(), view1.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(b);
        view1.draw(canvas);

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        final String randomKey = UUID.randomUUID().toString();
        File myPath = new File(directory, randomKey + ".jpg");
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(myPath);
            b.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            MediaStore.Images.Media.insertImage(getContentResolver(), b,
                    "Screen", "screen");

            openDialog();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        save.setVisibility(View.VISIBLE);

        return b;
    }

    private void openDialog () {
        Dialog dialog = new Dialog(AvatarMaker.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_avatar);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Button button = dialog.findViewById(R.id.cheers);
        button.setTypeface(ResourcesCompat.getFont(AvatarMaker.this, R.font.gloria_hallelujah));

        dialog.show();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}