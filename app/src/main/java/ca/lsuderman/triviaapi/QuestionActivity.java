package ca.lsuderman.triviaapi;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.List;

public class QuestionActivity extends AppCompatActivity {

    private RadioGroup radAnswers;
    private RadioButton radAnswer1, radAnswer2, radAnswer3, radAnswer4;
    private Button btnNextQuestion;
    private TextView txtQuestionCategory, txtQuestion;
    private int currentQuestionId = 0;
    private String answerGiven = "";
    private List<Question> questions;
    private Question currentQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        radAnswers = findViewById(R.id.radAnswers);
        radAnswer1 = findViewById(R.id.radAnswer1);
        radAnswer2 = findViewById(R.id.radAnswer2);
        radAnswer3 = findViewById(R.id.radAnswer3);
        radAnswer4 = findViewById(R.id.radAnswer4);
        btnNextQuestion = findViewById(R.id.btnNextQuestion);
        txtQuestionCategory = findViewById(R.id.txtQuestionCategory);
        txtQuestion = findViewById(R.id.txtQuestion);

        // gets all the questions that were created for the selected quiz
        int quizId = getIntent().getExtras().getInt("quizId");
        questions = ((TriviaDB) getApplicationContext()).getAllQuestionsByQuizId(quizId);

        // displays the first question
        displayCurrentQuestion();

        btnNextQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (radAnswer1.isChecked()) {
                    answerGiven = radAnswer1.getText().toString();
                }
                else if (radAnswer2.isChecked()){
                    answerGiven = radAnswer2.getText().toString();
                }
                else if (radAnswer3.isChecked()){
                    answerGiven = radAnswer3.getText().toString();
                }
                else if (radAnswer4.isChecked()){
                    answerGiven = radAnswer4.getText().toString();
                }

                ((TriviaDB) getApplicationContext()).addQuestionAnswerGiven(answerGiven, currentQuestion.getQuestionId());

                currentQuestionId++;
                // checks if there is another question
                if (currentQuestionId < questions.size()){
                    // displays next question
                    displayCurrentQuestion();
                    radAnswers.clearCheck();
                }
                else {
                    // display results
                    Intent intent = new Intent(getApplicationContext(), QuizResultsActivity.class);
                    intent.putExtra("quizId", quizId);
                    startActivity(intent);
                }
            }
        });
    }

    // TODO: fix text for quotes and others
    private void displayCurrentQuestion() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Question #" + (currentQuestionId + 1));
        }

        if (currentQuestionId + 1 == questions.size()){
            btnNextQuestion.setText("Finish Quiz");
        }

        currentQuestion = questions.get(currentQuestionId);
        txtQuestionCategory.setText("Category: " + currentQuestion.getCategory());
        txtQuestion.setText(currentQuestion.getQuestionString());

        List<String> incorrectAnswers = currentQuestion.getIncorrectAnswers();

        // TODO: put answers in random radButtons
        radAnswer1.setText(currentQuestion.getCorrectAnswer());
        radAnswer2.setText(incorrectAnswers.get(0));
        // if there is at least 2 incorrect answers, then it is a multiple choice question.
        // make options 3 and 4 available
        if (!incorrectAnswers.get(1).equals("")){
            radAnswer3.setText(incorrectAnswers.get(1));
            radAnswer4.setText(incorrectAnswers.get(2));

            radAnswer3.setVisibility(View.VISIBLE);
            radAnswer4.setVisibility(View.VISIBLE);
        }
        else {
            radAnswer3.setVisibility(View.GONE);
            radAnswer4.setVisibility(View.GONE);
        }
    }
}