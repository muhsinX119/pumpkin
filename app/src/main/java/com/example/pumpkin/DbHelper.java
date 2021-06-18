package com.example.pumpkin;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {

    public static final String ENTRIES_TABLE = "ENTRIES_TABLE";
    public static final String COLUMN_EXPENSE = "EXPENSE";
    public static final String COLUMN_TAG = "TAG";
    public static final String COLUMN_AMOUNT = "AMOUNT";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_DATE = "DATE";

    public DbHelper(@Nullable Context context) {
        super(context, "entries.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTableStatement = "CREATE TABLE " + ENTRIES_TABLE + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_EXPENSE + " TEXT, " + COLUMN_TAG + " TEXT, " + COLUMN_AMOUNT + " REAL, " + COLUMN_DATE + " INTEGER)";
        db.execSQL(createTableStatement);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addOne(DbStructure dbStructure) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_EXPENSE, dbStructure.getExpense());
        cv.put(COLUMN_TAG, dbStructure.getTag());
        cv.put(COLUMN_AMOUNT, dbStructure.getAmount());
        cv.put(COLUMN_DATE, dbStructure.getDate());

        db.insert(ENTRIES_TABLE, null, cv);
        db.close();


    }

    public List<DbStructure> getAll() {
        List<DbStructure> returnList = new ArrayList<>();

        String queryString = "SELECT * FROM " + ENTRIES_TABLE + " ORDER BY DATE DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Log.d("sqlite",db.getAttachedDbs().toString());
        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {

            do {
                int id = cursor.getInt(0);
                String expense = cursor.getString(1);
                String tag = cursor.getString(2);
                float amount = cursor.getFloat(3);
                long date = cursor.getLong(4);

                DbStructure readEntry = new DbStructure(id, expense, tag, amount, date);
                returnList.add(readEntry);

            } while (cursor.moveToNext());

        } else {
        }
        cursor.close();
        db.close();

        return returnList;
    }

    public float getExpenseSinceMonthStart() {
        //ArrayList<Float> returnList = new ArrayList<Float>();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        long startTime = cal.getTime().getTime();
        long endTime = System.currentTimeMillis();
        float sum=0.0f;


        String queryString = "SELECT SUM(AMOUNT) FROM " + ENTRIES_TABLE + " WHERE DATE BETWEEN "+ toString().valueOf(startTime)+" AND "+toString().valueOf(endTime);
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);
        cursor.moveToFirst();
        sum = cursor.getFloat(0);
        cursor.close();
        db.close();

        return sum;
    }

    public void updateOne(DbStructure dbStructure) {
        SQLiteDatabase db = this.getWritableDatabase();
        //ContentValues cv = new ContentValues();

        //cv.put(COLUMN_EXPENSE, dbStructure.getExpense());
        //cv.put(COLUMN_TAG, dbStructure.getTag());
        //cv.put(COLUMN_AMOUNT, dbStructure.getAmount());
        //cv.put(COLUMN_DATE, dbStructure.getDate());

        String sqlQueryUpdate = "UPDATE " + ENTRIES_TABLE + " SET " + COLUMN_EXPENSE + " = \"" + dbStructure.getExpense() + "\", " + COLUMN_TAG + " = \"" + dbStructure.getTag() + "\", " + COLUMN_AMOUNT + " = " + toString().valueOf(dbStructure.getAmount()) + ", " + COLUMN_DATE + " = " + toString().valueOf(dbStructure.getDate()) + " WHERE " + COLUMN_ID + " = " + toString().valueOf(dbStructure.getId());

        db.execSQL(sqlQueryUpdate);
        db.close();

    }

    public void deleteOne (String id) {

        SQLiteDatabase db = this.getWritableDatabase();
        String sqlQueryUpdate = "DELETE FROM " + ENTRIES_TABLE + " WHERE ID = "+id;
        db.execSQL(sqlQueryUpdate);
        db.close();

    }

    public ArrayList<String> getUniqueTags () {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> returnString = new ArrayList<String>();
        String queryString = "SELECT DISTINCT TAG FROM " + ENTRIES_TABLE;
        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {

            do {

                returnString.add(cursor.getString(0));

            } while (cursor.moveToNext());

        } else {
        }

        cursor.close();
        db.close();
        return returnString;
    }
}

