package com.example.myquizuserapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class QuestionsActivity extends AppCompatActivity {

    private TextView qustion, noIdecator;
    private FloatingActionButton bookmarkButn;
    LinearLayout optionContainer;
    Button shareButn, nextButton;
    List<QuestionModel> list;
    private int count = 0;
    private int potion = 0;
    int score = 0;
    private int macthQuePosition;
    private Dialog loadingdailog;

    ///shared prefarncee
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Gson gson;
    private List<QuestionModel> bookmarkslist;
    //////////
    public static final String FILE_NAME = "QUIZZER";
    public static final String KEY_NAME = "QUESTIONS";

    private String catogory;
    private int setNo;

    // Write a message to the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        Toolbar toolbar = findViewById(R.id.qusToolbar);
        setSupportActionBar(toolbar);

        loadingdailog = new Dialog(this);
        loadingdailog.setContentView(R.layout.loading_dailog);
        loadingdailog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_border));
        loadingdailog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingdailog.setCancelable(false);


        qustion = findViewById(R.id.questionId);
        noIdecator = findViewById(R.id.numberIndecatorId);
        bookmarkButn = findViewById(R.id.bookmarkButtonId);
        optionContainer = findViewById(R.id.optionContenierId);
        shareButn = findViewById(R.id.sharebuttonId);
        nextButton = findViewById(R.id.nextButtonId);

        preferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
        gson = new Gson();


        getBookmarks();
        bookmarkButn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modelMacth()) {

                    bookmarkslist.remove(macthQuePosition);
                    bookmarkButn.setImageDrawable(getDrawable(R.drawable.bookmark));
                } else {
                    bookmarkslist.add(list.get(potion));
                    bookmarkButn.setImageDrawable(getDrawable(R.drawable.ic_bookmark_name));

                }
            }
        });

        catogory = getIntent().getStringExtra("category");
        setNo = getIntent().getIntExtra("setno", 1);


        list = new ArrayList<>();
        loadingdailog.show();
        myRef.child("sets").child(catogory).child("quesution").orderByChild("setno").equalTo(setNo).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    list.add(snapshot.getValue(QuestionModel.class));
                }
                if (list.size() > 0) {

                    for (int i = 0; i < 4; i++) {
                        optionContainer.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                            @Override
                            public void onClick(View v) {
                                checkAnswer((Button) v);
                            }
                        });
                    }
                    playAnim(qustion, 0, list.get(potion).getQuestion());

                    nextButton.setOnClickListener(new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void onClick(View v) {
                            nextButton.setEnabled(false);
                            nextButton.setAlpha(0.7f);
                            enableOption(true);
                            potion++;
                            if (potion == list.size()) {
                                //score Activity
                                Intent scoreinten = new Intent(QuestionsActivity.this, ScoreActivity.class);
                                scoreinten.putExtra("scroe", score);
                                scoreinten.putExtra("total", list.size());
                                startActivity(scoreinten);
                                return;
                            }
                            count = 0;
                            playAnim(qustion, 0, list.get(potion).getQuestion());
                        }
                    });

                    shareButn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            String body = list.get(potion).getQuestion() + "\n"+
                                    list.get(potion).getOptionA() +"\n"+
                                    list.get(potion).getOptionB() +"\n"+
                                    list.get(potion).getOptionC() +"\n"+
                                    list.get(potion).getOptionC();
                            Intent sintent = new Intent(Intent.ACTION_SEND);
                            sintent.setType("text/plain");
                            sintent.putExtra(Intent.EXTRA_SUBJECT, "Quizzer challaenge");
                            sintent.putExtra(Intent.EXTRA_TEXT, body);
                            startActivity(Intent.createChooser(sintent, "Share via"));
                        }
                    });


                } else {
                    finish();
                    Toast.makeText(QuestionsActivity.this, "No Qusention", Toast.LENGTH_SHORT).show();
                }
                loadingdailog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(QuestionsActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                loadingdailog.dismiss();
                finish();
            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        storeBookmarks();
    }

    ////////playAnimation//////////
    private void playAnim(final View view, final int value, final String data) {
        view.animate().alpha(value).scaleX(value).scaleY(value).setDuration(500).setStartDelay(100).setInterpolator(new DecelerateInterpolator())
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                        if (value == 0 && count < 4) {
                            String option = "";
                            if (count == 0) {
                                option = list.get(potion).getOptionA();
                            } else if (count == 1) {
                                option = list.get(potion).getOptionB();
                            } else if (count == 2) {
                                option = list.get(potion).getOptionC();
                            } else if (count == 3) {
                                option = list.get(potion).getOptionD();
                            }
                            playAnim(optionContainer.getChildAt(count), 0, option);
                            count++;
                        }
                    }

                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        //data change
                        if (value == 0) {
                            try {
                                ((TextView) view).setText(data);
                                noIdecator.setText(potion + 1 + "/" + list.size());
                                if (modelMacth()) {

                                    bookmarkButn.setImageDrawable(getDrawable(R.drawable.ic_bookmark_name));

                                } else {
                                    bookmarkButn.setImageDrawable(getDrawable(R.drawable.bookmark));

                                }
                            } catch (ClassCastException ex) {
                                ((Button) view).setText(data);
                            }
                            view.setTag(data);
                            playAnim(view, 1, data);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
    }

    ///////checkAnswer//////////
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void checkAnswer(Button selectedOption) {
        enableOption(false);
        nextButton.setEnabled(true);
        nextButton.setAlpha(1);
        if (selectedOption.getText().toString().equals(list.get(potion).getCorrectAns())) {
            //correct
            score++;
            selectedOption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));

        } else {
            //incorrect
            selectedOption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ff0000")));
            Button correctOption = (Button) optionContainer.findViewWithTag(list.get(potion).getCorrectAns());
            correctOption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));

        }
    }

    /////////button enable///////
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

    private void enableOption(boolean enable) {
        for (int i = 0; i < 4; i++) {
            optionContainer.getChildAt(i).setEnabled(enable);
            if (enable) {
                optionContainer.getChildAt(i).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#9E9C9C")));

            }
        }
    }

    private void getBookmarks() {
        String json = preferences.getString(KEY_NAME, "");
        Type type = new TypeToken<List<QuestionModel>>() {
        }.getType();
        bookmarkslist = gson.fromJson(json, type);
        if (bookmarkslist == null) {
            bookmarkslist = new ArrayList<>();

        }
    }

    private boolean modelMacth() {
        boolean macth = false;
        int i = 0;
        for (QuestionModel model : bookmarkslist) {
            i++;
            if (model.getQuestion().equals(list.get(potion).getQuestion())
                    && model.getCorrectAns().equals(list.get(potion).getCorrectAns())
                    && model.getSetno() == list.get(potion).getSetno()) {
                macth = true;
                macthQuePosition = i;
            }
            i++;
        }
        return macth;


    }

    private void storeBookmarks() {
        String json = gson.toJson(bookmarkslist);
        editor.putString(KEY_NAME, json);
        editor.commit();

    }
}
