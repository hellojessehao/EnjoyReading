package com.example.zhangshihao.mytestapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.zhangshihao.mytestapplication.novel.NovelActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    //onclick button novel
    public void gotoNovel(View view){
        startActivity(new Intent(this, NovelActivity.class));
    }

}
