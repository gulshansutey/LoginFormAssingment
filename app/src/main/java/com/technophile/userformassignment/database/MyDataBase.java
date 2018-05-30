package com.technophile.userformassignment.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;

public class MyDataBase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "contactsManager";
    public static final String TABLE_QNA = "qna";
    public static final String TABLE_NAME = "user";
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_MOBILE = "mobile";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_GENDER = "gender";
    public static final String KEY_AGE = "age";
    public static final String KEY_PIC = "picture";
    public static final String DT_TEXT = " TEXT ";

    public MyDataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {

            String CREATE_TABLE="CREATE TABLE "+TABLE_NAME+"("+KEY_ID+DT_TEXT+","+KEY_EMAIL+DT_TEXT+","+
                    KEY_NAME+DT_TEXT+","+KEY_GENDER+DT_TEXT+","+KEY_MOBILE+DT_TEXT+","+KEY_PIC+DT_TEXT+","+KEY_AGE+DT_TEXT+")";



            db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addUser(ContentValues user_info){
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_NAME, null, user_info);
        db.close();
    }

    public HashMap<String,String>getUserInfo(){
        SQLiteDatabase db = this.getReadableDatabase();
        HashMap<String,String>data=new HashMap<>();
        Cursor cursor = db.query(TABLE_NAME,null, null, null, null, null, null);
        if (cursor != null){
            cursor.moveToFirst();
            data.put(KEY_NAME,cursor.getString(cursor.getColumnIndex(KEY_NAME)));
            data.put(KEY_EMAIL,cursor.getString(cursor.getColumnIndex(KEY_EMAIL)));
            data.put(KEY_GENDER,cursor.getString(cursor.getColumnIndex(KEY_GENDER)));
            data.put(KEY_PIC,cursor.getString(cursor.getColumnIndex(KEY_PIC)));
            data.put(KEY_MOBILE,cursor.getString(cursor.getColumnIndex(KEY_MOBILE)));
            data.put(KEY_ID,cursor.getString(cursor.getColumnIndex(KEY_ID)));
            data.put(KEY_AGE,cursor.getString(cursor.getColumnIndex(KEY_AGE)));
            cursor.close();
        }
        return data;
    }

    public void deleteDB(){
        SQLiteDatabase db = this.getWritableDatabase(); // helper is object extends SQLiteOpenHelper
        db.delete(TABLE_NAME, null, null);

    }

    public int updateInfo(ContentValues user_info) {
        SQLiteDatabase db = this.getWritableDatabase();
       int re= db.update(TABLE_NAME, user_info, KEY_ID + " = ?",
                new String[] { String.valueOf(user_info.get(KEY_ID))});
        db.close();
        return re;
    }

}
