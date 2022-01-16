package com.example.pai;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MDH extends SQLiteOpenHelper {

    private Context context;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://paiimages-default-rtdb.europe-west1.firebasedatabase.app")
            .getReference().child("statistic");
    static SQLiteDatabase sqliteDataBase;
    private static String DB_PATH = "/data/data/com.example.pai/databases/";
    private static final String DATABASE_NAME = "Pai.db";
    private static final int DATABASE_VERSION = 1;

    public MDH(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

//    public void createDataBase() throws IOException {
//        if(checkDataBase()) {
//            // do nothing, feel relax
//        }
//        else {
//            Log.d("AAAAAAAA", "database do not exist, try to copy");
//            this.getReadableDatabase();
//            copyDataBase();
//        }
//    }
//
//    public boolean checkDataBase(){
//        File databaseFile = new File(DB_PATH + DATABASE_NAME);
//        return databaseFile.exists();
//    }
//
//    private void copyDataBase() throws IOException {
//        //Open your local db as the input stream
//        InputStream myInput = context.getAssets().open(DATABASE_NAME);
//        // Path to the just created empty db
//        String outFileName = DB_PATH + DATABASE_NAME;
//        //Open the empty db as the output stream
//        OutputStream myOutput = new FileOutputStream(outFileName);
//        //transfer bytes from the input file to the output file
//        byte[] buffer = new byte[1024];
//        int length;
//        while ((length = myInput.read(buffer))>0){
//            myOutput.write(buffer, 0, length);
//        }
//        myOutput.flush();
//        myOutput.close();
//        myInput.close();
//    }
//
//    public void openDataBase() throws SQLException {
//        //Open the database
//        String myPath = DB_PATH + DATABASE_NAME;
//        sqliteDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
//    }
//
//    @Override
//    public synchronized void close() {
//        if(sqliteDataBase != null)
//            sqliteDataBase.close();
//        super.close();
//    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query =
                "CREATE TABLE anime_test " +
                        "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "image_name TEXT, " +
                        "image_format TEXT, " +
                        "first_q TEXT, " +
                        "second_q TEXT, " +
                        "third_q TEXT, " +
                        "fourth_q TEXT, " +
                        "answer INTEGER);";
        String queryKiss =
                "CREATE TABLE kiss_test " +
                        "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "image_f TEXT, " +
                        "image_s TEXT, " +
                        "image_t TEXT);";
        String queryChar =
                "CREATE TABLE characters " +
                        "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "image_name TEXT, " +
                        "name TEXT, " +
                        "title TEXT);";

        db.execSQL(query);
        db.execSQL(queryKiss);
        db.execSQL(queryChar);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS anime_test");
        db.execSQL("DROP TABLE IF EXISTS kiss_test");
        db.execSQL("DROP TABLE IF EXISTS characters");
        onCreate(db);
    }

    public void addQuestion(String imageName, String imageExt, String first, String second, String third, String fourth, int answer) {
//        SQLiteDatabase db = this.getWritableDatabase();
        sqliteDataBase = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("image_name", imageName);
        cv.put("image_format", imageExt);
        cv.put("first_q", first);
        cv.put("second_q", second);
        cv.put("third_q", third);
        cv.put("fourth_q", fourth);
        cv.put("answer", answer);
        long result = sqliteDataBase.insert("anime_test", null, cv);
        if(result == -1) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Added", Toast.LENGTH_SHORT).show();
        }
    }

    public void addRound(String imageF, String imageS, String imageT) {
        sqliteDataBase = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("image_f", imageF);
        cv.put("image_s", imageS);
        cv.put("image_t", imageT);
        long result = sqliteDataBase.insert("kiss_test", null, cv);
        if(result == -1) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Added", Toast.LENGTH_SHORT).show();
        }
    }

    public void addCharacter(String image, String name, String title) {
        sqliteDataBase = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("image_name", image);
        cv.put("name", name);
        cv.put("title", title);
        long result = sqliteDataBase.insert("characters", null, cv);
        if(result == -1) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Added", Toast.LENGTH_SHORT).show();
        }
    }

    public void addStatistic(String id) {
        DatabaseReference db = databaseReference;
        Statistic stat = new Statistic(0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        db.child(id).setValue(stat);
    }

    public void editStatistic(String id, String kiss, String marry, String kill, Statistic statistic) {
        DatabaseReference db = databaseReference;

        if(statistic != null){
            switch (kiss) {
                case "one":
                    statistic.setKiss_one(statistic.getKiss_one()+1);
                    break;
                case "two":
                    statistic.setKiss_two(statistic.getKiss_two()+1);
                    break;
                case "thr":
                    statistic.setKiss_th(statistic.getKiss_th()+1);
                    break;
            }
            switch (marry) {
                case "one":
                    statistic.setMarry_one(statistic.getMarry_one()+1);
                    break;
                case "two":
                    statistic.setMarry_two(statistic.getMarry_two()+1);
                    break;
                case "thr":
                    statistic.setMarry_th(statistic.getMarry_th()+1);
                    break;
            }
            switch (kill) {
                case "one":
                    statistic.setKill_one(statistic.getKill_one()+1);
                    break;
                case "two":
                    statistic.setKill_two(statistic.getKill_two()+1);
                    break;
                case "thr":
                    statistic.setKill_th(statistic.getKill_th()+1);
                    break;
            }
            statistic.setTotal(statistic.getTotal()+1);

            db.child(id).setValue(statistic);
        }
    }

    public void existOrCreate(String id) {
        DatabaseReference db = databaseReference.child(id);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()) {
                    addStatistic(id);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("MDH-ifExist", error.getMessage());
            }
        };
        db.addListenerForSingleValueEvent(eventListener);
    }

    public Cursor getQuestion(int rowNumber) {
//        String query = "SELECT * FROM (" +
//                "SELECT " +
//                "_id, first_q, second_q, third_q, fourth_q, answer, image_name, image_format, ROW_NUMBER() OVER(ORDER BY _id) AS ROW " +
//                "FROM anime_test" +
//                ") AS TMP " +
//                "WHERE ROW = " + rowNumber + ";";
        String query = "SELECT _id, first_q, second_q, third_q, fourth_q, answer, image_name, image_format " +
                "FROM anime_test " +
                "WHERE _id=" + rowNumber + ";";

        sqliteDataBase = this.getReadableDatabase();

        Cursor cursor = null;
        if(sqliteDataBase != null) {
            cursor = sqliteDataBase.rawQuery(query, null);
        }
        return cursor;
    }

    public Cursor getRound(int rowNumber) {
        String query = "SELECT _id, image_f, image_s, image_t " +
                "FROM kiss_test " +
                "WHERE _id=" + rowNumber + ";";

        sqliteDataBase = this.getReadableDatabase();

        Cursor cursor = null;
        if(sqliteDataBase != null) {
            cursor = sqliteDataBase.rawQuery(query, null);
        }
        return cursor;
    }

    public Cursor getCharacter(String imageName) {
        String query = "SELECT _id, name, title " +
                "FROM characters " +
                "WHERE image_name='" + imageName + "';";

        sqliteDataBase = this.getReadableDatabase();

        Cursor cursor = null;
        if(sqliteDataBase != null) {
            cursor = sqliteDataBase.rawQuery(query, null);
        }
        return cursor;
    }

    public void editStatistic(String id, String kiss, String marry, String kill) {
        DatabaseReference db = databaseReference.child(id);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Statistic statistic = snapshot.getValue(Statistic.class);
                editStatistic(id, kiss, marry, kill, statistic);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("MDH-ifExist", error.getMessage());
            }
        };
        db.addListenerForSingleValueEvent(valueEventListener);
    }

}

