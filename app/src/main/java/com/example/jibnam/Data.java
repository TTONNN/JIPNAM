package com.example.jibnam;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class Data extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Jibnam.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "water_logs";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_TIME = "time";

    public Data(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_AMOUNT + " INTEGER, " +
                COLUMN_TIME + " TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // เพิ่มข้อมูล
    public boolean insertWaterLog(WaterLog log) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_AMOUNT, log.getAmount());
        cv.put(COLUMN_TIME, log.getTime());

        long result = db.insert(TABLE_NAME, null, cv);
        return result != -1;
    }

    // ดึงประวัติทั้งหมด
    public List<WaterLog> getAllLogs() {
        List<WaterLog> logList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_ID + " DESC", null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                int amount = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT));
                String time = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME));

                logList.add(new WaterLog(id, amount, time));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return logList;
    }
}
