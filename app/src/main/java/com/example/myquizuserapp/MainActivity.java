package com.example.myquizuserapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

  private   Button startButton,bookmarkbtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       startButton=findViewById(R.id.startBtnId);
       bookmarkbtn=findViewById(R.id.bookMarkBtnId);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent categoryIntent=new Intent(MainActivity.this, CategoryActivity.class);
                startActivity(categoryIntent);
            }
        });

        bookmarkbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bookmarksIntent=new Intent(MainActivity.this, BookmarkActivity.class);
                startActivity(bookmarksIntent);
            }
        });
    }
}
