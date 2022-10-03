package ca.lsuderman.triviaapi;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

public class QuizResultsActivity extends AppCompatActivity {

    private Button btnHome, btnRetakeQuiz;
    private TextView txtResult, txtDetails;
    private List<Question> questions;
    private int questionNumber = 1;
    private static final DecimalFormat df = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_results);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Quiz Results");
        }

        btnHome = findViewById(R.id.btnHome);
        btnRetakeQuiz = findViewById(R.id.btnRetakeQuiz);
        txtResult = findViewById(R.id.txtResult);
        txtDetails = findViewById(R.id.txtDetails);

        int quizId = getIntent().getExtras().getInt("quizId");
        questions = ((TriviaDB) getApplicationContext()).getAllQuestionsByQuizId(quizId);

        int correctAnswers = 0;
        String details = "";

        // displays the answers that was selected, and the correct answer for each question in the quiz
        // TODO: figure out why it doesn't replace the HTML characters
        for (Question question: questions) {
            String correctAnswer = question.getCorrectAnswer();
            correctAnswer = Utilities.replaceHTMLCharacters(correctAnswer);

            String answerGiven = question.getAnswerGiven();
            answerGiven = Utilities.replaceHTMLCharacters(answerGiven);

            details += questionNumber + ": " + question.getQuestionString() + "\n" +
                    "Correct Answer: " + correctAnswer +
                    "\nYour Answer: " + answerGiven +
                    "\n\n";
            if (question.getCorrectAnswer().equals(question.getAnswerGiven())){
                correctAnswers++;
            }
            questionNumber++;
        }

        try {
            ((TriviaDB) getApplicationContext()).setCorrectAnswers(quizId, correctAnswers);
        } catch (Exception exception) {
            Log.e("exception", exception.toString());
        }

        // calculates the quiz score and displays it on the screen
        double average = (double) correctAnswers / (double) questions.size() * 100.0;
        txtResult.setText("You got " + correctAnswers + " out of " + questions.size() + " correct (" + df.format(average) + "%)");

        txtDetails.setText(details);

        // clicks to go to the home page
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        // clicks to retake the quiz
        btnRetakeQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Restarts the quiz
                Intent intent = new Intent(getApplicationContext(), QuestionActivity.class);
                intent.putExtra("quizId", quizId);
                startActivity(intent);
            }
        });
    }
}