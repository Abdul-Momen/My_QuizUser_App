package com.example.myquizuserapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.animation.Animator;
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

    private String catogory;
    private int setNo;

    // Write a message to the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        Toolbar toolbar = findViewById(R.id.qusToolbar);
        setSupportActionBar(toolbar);


        qustion = findViewById(R.id.questionId);
        noIdecator = findViewById(R.id.numberIndecatorId);
        bookmarkButn = findViewById(R.id.bookmarkButtonId);
        optionContainer = findViewById(R.id.optionContenierId);
        shareButn = findViewById(R.id.sharebuttonId);
        nextButton = findViewById(R.id.nextButtonId);


        catogory = getIntent().getStringExtra("category");
        setNo = getIntent().getIntExtra("setno", 1);


        list = new ArrayList<>();

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

                                return;
                            }
                            count = 0;
                            playAnim(qustion, 0, list.get(potion).getQuestion());
                        }
                    });

                } else {
                    finish();
                    Toast.makeText(QuestionsActivity.this, "No Qusention", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(QuestionsActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


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

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        //data change
                        if (value == 0) {
                            try {
                                ((TextView) view).setText(data);
                                noIdecator.setText(potion + 1 + "/" + list.size());
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


}
