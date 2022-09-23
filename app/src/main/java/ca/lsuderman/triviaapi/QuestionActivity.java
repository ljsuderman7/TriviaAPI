package ca.lsuderman.triviaapi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
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
    private TextView txtQuestionNumber, txtQuestion;
    private int quizId, currentQuestionId = 0;
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
        txtQuestionNumber = findViewById(R.id.txtQuestionNumber);
        txtQuestion = findViewById(R.id.txtQuestion);

        quizId = getIntent().getExtras().getInt("quizId");
        questions = ((TriviaDB) getApplicationContext()).getAllQuestionsByQuizId(quizId);

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
                Log.d("Answer Given", answerGiven);
                Log.d("Correct Answer", currentQuestion.getCorrectAnswer());
                if (answerGiven.equals(currentQuestion.getCorrectAnswer())){
                    Log.d("Got Correct Answer", "True");
                }
                else {
                    Log.d("Got Correct Answer", "False");
                }

                currentQuestionId++;
                // check if there is another question
                if (currentQuestionId < questions.size()){
                    displayCurrentQuestion();
                    radAnswers.clearCheck();
                }
                else {
                    // display results
                }
            }
        });
    }

    private void displayCurrentQuestion() {
        currentQuestion = questions.get(currentQuestionId);
        txtQuestionNumber.setText("Question " + (currentQuestionId + 1) + " of " + questions.size() + ": " + currentQuestion.getCategory());
        txtQuestion.setText(currentQuestion.getQuestionString());

        List<String> incorrectAnswers = currentQuestion.getIncorrectAnswers();

        // TODO: put answers in random radButtons
        radAnswer1.setText(currentQuestion.getCorrectAnswer());
        radAnswer2.setText(incorrectAnswers.get(0));
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