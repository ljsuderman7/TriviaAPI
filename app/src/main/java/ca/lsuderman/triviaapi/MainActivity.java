package ca.lsuderman.triviaapi;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private Button btnGenerate, btnViewQuizzes;
    private TextView txtTotalQuizzes, txtAllAverage, txtPerfectQuizzes, txtQuestionsAnswered, txtLatestQuiz;
    private LinearLayout llStats;
    private List<Question> questions, latestQuizQuestions;
    private Quiz latestQuiz;
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
        txtLatestQuiz = findViewById(R.id.txtLatestQuiz);
        llStats = findViewById(R.id.llStats);

        int totalQuizzes = 0;
        try {
            questions = ((TriviaDB) getApplicationContext()).getAllQuestions();
            totalQuizzes = ((TriviaDB) getApplicationContext()).getNextQuizId() - 1;
            latestQuizQuestions = ((TriviaDB) getApplicationContext()).getAllQuestionsByQuizId(totalQuizzes);
            latestQuiz = ((TriviaDB) getApplicationContext()).getQuiz(totalQuizzes);
        } catch (Exception exception) {
            // no-op
        }

        displayStats(totalQuizzes);

        // goes to quiz setup page
        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CreateQuizActivity.class));
            }
        });

        // goes to view all quizzes page
        btnViewQuizzes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ViewQuizzesActivity.class);
                intent.putExtra("quizId", 0);
                intent.putExtra("singleDelete", false);
                startActivity(intent);
            }
        });
    }

    private void displayStats(int totalQuizzes){
        if (questions.size() > 0) {
            // total number of questions answered
            txtQuestionsAnswered.setText(String.valueOf(questions.size()));

            // total number of quizzes taken
            txtTotalQuizzes.setText(String.valueOf(totalQuizzes));

            // total number of perfect quizzes
            txtPerfectQuizzes.setText(getPerfectQuizzes(totalQuizzes));

            // average of all quizzes
            double average = getAllAverage();
            txtAllAverage.setText(df.format(average) + "%");

            // latest quiz info
            int correctAnswers = 0;
            for (Question question: latestQuizQuestions) {
                if (question.getAnswerGiven().equals(question.getCorrectAnswer())) {
                    correctAnswers++;
                }
            }

            double latestQuizAverage = (double) latestQuiz.getCorrectAnswers() / (double) latestQuiz.getNumberOfQuestions() * 100;

            String latestQuizInfo = "Quiz #" + latestQuiz.getId() +
                    "\nCategory: " + latestQuiz.getCategory() +
                    "\nDifficulty: " + latestQuiz.getDifficulty() +
                    "\nQuestion Type: " + latestQuiz.getQuestionType() +
                    "\nScore: " + String.valueOf(latestQuiz.getCorrectAnswers()) + "/" + String.valueOf(latestQuiz.getNumberOfQuestions()) + " (" + df.format(latestQuizAverage) + "%)" +
                    "\n";

            txtLatestQuiz.setText(latestQuizInfo);

            llStats.setVisibility(View.VISIBLE);
        }
        else {
            llStats.setVisibility(View.GONE);
        }
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