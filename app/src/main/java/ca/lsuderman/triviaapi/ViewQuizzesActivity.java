package ca.lsuderman.triviaapi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.divider.MaterialDivider;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ViewQuizzesActivity extends AppCompatActivity {

    private LinearLayout llQuizzes, llDeleteButton;
    private Button btnDeleteAllQuizzes;
    private static final DecimalFormat df = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_quizzes);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Previous Quizzes");
        }

        llQuizzes = findViewById(R.id.llQuizzes);
        llDeleteButton = findViewById(R.id.llDeleteButton);
        btnDeleteAllQuizzes = findViewById(R.id.btnDeleteAllQuizzes);

        displayQuizzes();

        // delete all quizzes
        btnDeleteAllQuizzes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    ((TriviaDB) getApplicationContext()).deleteAllQuestions();
                    ((TriviaDB) getApplicationContext()).deleteAllQuizzes();
                } catch (Exception exception) {
                    Log.d("Exception", exception.toString());
                }

                llQuizzes.removeAllViews();
                displayQuizzes();
            }
        });
    }

    private void displayQuizzes(){
        List<Quiz> quizzes = new ArrayList<>();

        try {
            quizzes = ((TriviaDB) getApplicationContext()).getAllQuizzes();
        } catch (Exception exception) {
            // no-op
        }

        for (Quiz quiz: quizzes) {
            // if it is not the first quiz, add a divider
            if (quiz.getId() != 1) {
                // just adds a bit of space above the divider
                TextView aboveDivider = new TextView(ViewQuizzesActivity.this);
                aboveDivider.setText("");
                llQuizzes.addView(aboveDivider);

                // adds the divider
                MaterialDivider divider = new MaterialDivider(ViewQuizzesActivity.this);
                llQuizzes.addView(divider);

                // just adds a bit of space below the divider
                TextView belowDivider = new TextView(ViewQuizzesActivity.this);
                belowDivider.setText("");
                llQuizzes.addView(belowDivider);
            }

            // the outer layout that contains the details and buttons for each individual quiz
            LinearLayout quizLayout = new LinearLayout(ViewQuizzesActivity.this);
            quizLayout.setOrientation(LinearLayout.VERTICAL);

            // contains the buttons of the quiz
            LinearLayout buttonsLayout = new LinearLayout(ViewQuizzesActivity.this);
            buttonsLayout.setOrientation(LinearLayout.HORIZONTAL);

            // the actual details of the quiz
            TextView txtDetails = new TextView(ViewQuizzesActivity.this);
            double latestQuizAverage = (double) quiz.getCorrectAnswers() / (double) quiz.getNumberOfQuestions() * 100;
            String details = "Quiz #" + quiz.getId() +
                    "\nCategory: " + quiz.getCategory() +
                    "\nDifficulty: " + quiz.getDifficulty() +
                    "\nQuestion Type: " + quiz.getQuestionType() +
                    "\nScore: " + String.valueOf(quiz.getCorrectAnswers()) + "/" + String.valueOf(quiz.getNumberOfQuestions()) + " (" + df.format(latestQuizAverage) + "%)";
            txtDetails.setText(details);

            // button to retake the quiz
            Button btnRetake = new Button(ViewQuizzesActivity.this);
            btnRetake.setText("Retake");
            btnRetake.setBackground(getResources().getDrawable(R.drawable.button_bg));
            btnRetake.setTextColor(getResources().getColor(R.color.white));
            btnRetake.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            btnRetake.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), QuestionActivity.class);
                    intent.putExtra("quizId", quiz.getId());
                    startActivity(intent);
                }
            });

            // button to delete the quiz
            Button btnDeleteQuiz = new Button(ViewQuizzesActivity.this);
            btnDeleteQuiz.setText("Delete");
            btnDeleteQuiz.setBackground(getResources().getDrawable(R.drawable.button_bg));
            btnDeleteQuiz.setTextColor(getResources().getColor(R.color.white));
            btnDeleteQuiz.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            btnDeleteQuiz.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        ((TriviaDB) getApplicationContext()).deleteQuiz(quiz.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    llQuizzes.removeAllViews();
                    displayQuizzes();
                }
            });

            // first add the details to the layout
            quizLayout.addView(txtDetails);

            // add the first button the the button layout
            buttonsLayout.addView(btnRetake);

            // add a bit of a space between the buttons
            TextView buttonSpacing = new TextView(ViewQuizzesActivity.this);
            buttonSpacing.setText("   ");
            buttonsLayout.addView(buttonSpacing);

            // add the second button to the button layout
            buttonsLayout.addView(btnDeleteQuiz);

            // add the button layout to the outer layout
            quizLayout.addView(buttonsLayout);

            // add the details of the quiz to the screen
            llQuizzes.addView(quizLayout);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        boolean result = true;
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
            default:
                result = super.onOptionsItemSelected(item);
                break;
        }
        return result;
    }
}