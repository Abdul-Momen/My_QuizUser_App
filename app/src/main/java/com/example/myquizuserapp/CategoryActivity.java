package com.example.myquizuserapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Toolbar toolbarx;

    private Dialog loadingdailog;

    private List<CatagoreyModel> list;

    // Write a message to the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catagoris);

        recyclerView = findViewById(R.id.categoryRecyclerViewId);
        toolbarx = findViewById(R.id.toolbarId);

        loadingdailog = new Dialog(this);
        loadingdailog.setContentView(R.layout.loading_dailog);
        loadingdailog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_border));
        loadingdailog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingdailog.setCancelable(false);

        setSupportActionBar(toolbarx);
        getSupportActionBar().setTitle("Category");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);


        list = new ArrayList<>();


        final CatagoreyAdapter adapter = new CatagoreyAdapter(list);
        recyclerView.setAdapter(adapter);


        loadingdailog.show();

        myRef.child("Categorys").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    list.add(dataSnapshot1.getValue(CatagoreyModel.class));
                }
                adapter.notifyDataSetChanged();
                loadingdailog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(CategoryActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                loadingdailog.dismiss();
                finish();
            }
        });


    }
}
