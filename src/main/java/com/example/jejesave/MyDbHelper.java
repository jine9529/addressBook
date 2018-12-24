package com.example.jejesave;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDbHelper extends SQLiteOpenHelper {

    private static MyDbHelper instance;

    public MyDbHelper(Context context) {
        super(context, "mytest.db", null, 2);
    }

    static public synchronized MyDbHelper getInstance(Context context) {
        if(instance == null)
            instance = new MyDbHelper(context);
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE people" +
        "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, fone TEXT, nick TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS people;");
        onCreate(sqLiteDatabase);
    }
}
