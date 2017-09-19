package com.example.zhangshihao.mytestapplication.novel.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by zhangshihao on 2017/9/19.
 */

public class NovelOpenHelper extends SQLiteOpenHelper {

    private static NovelOpenHelper mNovelOpenHelper;

    public NovelOpenHelper(Context context) {
        super(context, "bookmark.db", null, 1);
    }

    public static NovelOpenHelper getInstance(Context context){
        if (mNovelOpenHelper == null){
            mNovelOpenHelper = new NovelOpenHelper(context);
        }
        return mNovelOpenHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table bookmark (_id integer primary key autoincrement,novelname text," +
                "sectionname text,sectionurl text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void insertData(String novelName,String sectionName,String sectionUrl){
        SQLiteDatabase db = mNovelOpenHelper.getWritableDatabase();
        String insertSQL = "insert into bookmark (novelname,sectionname,sectionurl) values(?,?,?)";
        db.execSQL(insertSQL,new String[]{novelName,sectionName,sectionUrl});
    }

    public void deleteData(String sectionUrl){
        SQLiteDatabase db = mNovelOpenHelper.getWritableDatabase();
        String deleteSQL = "delete from bookmark where sectionUrl=?";
        db.execSQL(deleteSQL,new String[]{sectionUrl});
    }

    public void deleteAllData(){
        SQLiteDatabase db = mNovelOpenHelper.getWritableDatabase();
        db.execSQL("delete from bookmark where 1=1");
    }

    public Cursor selectDataFromNovelName(String novelName){
        SQLiteDatabase db = mNovelOpenHelper.getReadableDatabase();
        Cursor cursor = db.query("bookmark",null,"novelname = ?",new String[]{novelName},null,null,"_id");
        return cursor;
    }

    public Cursor selectAllData(){
        SQLiteDatabase db = mNovelOpenHelper.getReadableDatabase();
        Cursor cursor = db.query("bookmark",null,null,null,null,null,"novelname");
        return cursor;
    }

}
