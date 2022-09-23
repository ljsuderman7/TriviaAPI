package ca.lsuderman.triviaapi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class QuizResultsActivity extends AppCompatActivity {

    private Button btnHome;
    private TextView txtResult, txtDetails;
    private List<Question> questions;
    private int questionNumber = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_results);

        btnHome = findViewById(R.id.btnHome);
        txtResult = findViewById(R.id.txtResult);
        txtDetails = findViewById(R.id.txtDetails);

        int quizId = getIntent().getExtras().getInt("quizId");
        questions = ((TriviaDB) getApplicationContext()).getAllQuestionsByQuizId(quizId);

        int correctAnswers = 0;
        String details = "";

        for (Question question: questions) {
            details += questionNumber + ": " + question.getQuestionString() + "\n" +
                    "Correct Answer: " + question.getCorrectAnswer() + "\n" +
                    "Your Answer: " + question.getAnswerGiven() + "\n\n";
            if (question.getCorrectAnswer().equals(question.getAnswerGiven())){
                correctAnswers++;
            }
            questionNumber++;
        }

        double average = (double) correctAnswers / (double) questions.size() * 100.0;
        txtResult.setText("You got " + correctAnswers + " out of " + questions.size() + " correct (" + average + "%)");

        txtDetails.setText(details);

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }
}