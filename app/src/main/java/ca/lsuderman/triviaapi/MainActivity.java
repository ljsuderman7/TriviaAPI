package ca.lsuderman.triviaapi;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private Button btnGenerate, btnViewQuizzes;
    private TextView txtTotalQuizzes, txtAllAverage, txtPerfectQuizzes, txtQuestionsAnswered;
    private List<Question> questions;
    private static final DecimalFormat df = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Home");
        }

        btnGenerate = findViewById(R.id.btnGenerate);
        btnViewQuizzes = findViewById(R.id.btnViewQuizzes);
        txtTotalQuizzes = findViewById(R.id.txtTotalQuizzes);
        txtAllAverage = findViewById(R.id.txtAllAverage);
        txtPerfectQuizzes = findViewById(R.id.txtPerfectQuizzes);
        txtQuestionsAnswered = findViewById(R.id.txtQuestionsAnswered);

        //TriviaDataService triviaDataService = new TriviaDataService(MainActivity.this);

        int totalQuizzes = 0;
        try {
            questions = ((TriviaDB) getApplicationContext()).getAllQuestions();
            totalQuizzes = ((TriviaDB) getApplicationContext()).getNextQuizId() - 1;
        } catch (Exception exception) {
            // no-op
        }

        // total number of questions answered
        txtQuestionsAnswered.setText(String.valueOf(questions.size()));

        // total number of quizzes taken
        txtTotalQuizzes.setText(String.valueOf(totalQuizzes));

        // total number of perfect quizzes
        txtPerfectQuizzes.setText(getPerfectQuizzes(totalQuizzes));

        // average of all quizzes
        double average = getAllAverage();
        txtAllAverage.setText(df.format(average) + "%");

        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CreateQuizActivity.class));
            }
        });

        btnViewQuizzes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: create activity
            }
        });
    }

    // returns the amount of quizzes with a perfect score
    private String getPerfectQuizzes(int totalQuizzes){
        int totalPerfectQuizzes = 0;

        for (int i = 1; i <= totalQuizzes; i++) {
            List<Question> quiz = new ArrayList<>();
            try {
                quiz = ((TriviaDB) getApplicationContext()).getAllQuestionsByQuizId(i);
            } catch (Exception exception) {
                // no-op
            }
            boolean perfectQuiz = true;
            for (Question question: quiz){
                if (!question.getAnswerGiven().equals(question.getCorrectAnswer())) {
                    perfectQuiz = false;
                }
            }
            if (perfectQuiz) {
                totalPerfectQuizzes++;
            }
        }

        return String.valueOf(totalPerfectQuizzes);
    }

    // returns the average of all the questions answered correctly
    private double getAllAverage(){
        double average = 0.0;
        int correct = 0;

        for (Question question: questions) {
            if (question.getAnswerGiven().equals(question.getCorrectAnswer())) {
                correct++;
            }
        }

        average = (double) correct / (double) questions.size() * 100;

        return average;
    }
}