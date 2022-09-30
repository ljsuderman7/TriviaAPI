package ca.lsuderman.triviaapi;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Random;

public class QuestionActivity extends AppCompatActivity {

    private RadioGroup radAnswers;
    private RadioButton radAnswer1, radAnswer2, radAnswer3, radAnswer4;
    private Button btnNextQuestion, btnPreviousQuestion;
    private TextView txtQuestionCategory, txtQuestion;
    private int currentQuestionId = 0, quizId;
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
        btnPreviousQuestion = findViewById(R.id.btnPreviousQuestion);
        txtQuestionCategory = findViewById(R.id.txtQuestionCategory);
        txtQuestion = findViewById(R.id.txtQuestion);

        // gets all the questions that were created for the selected quiz
        quizId = getIntent().getExtras().getInt("quizId");
        questions = ((TriviaDB) getApplicationContext()).getAllQuestionsByQuizId(quizId);

        // displays the first question
        displayCurrentQuestion();

        // goes to the next question
        btnNextQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (radAnswer1.isChecked()) {
                    answerGiven = radAnswer1.getText().toString();
                    selectAnswer();
                }
                else if (radAnswer2.isChecked()){
                    answerGiven = radAnswer2.getText().toString();
                    selectAnswer();
                }
                else if (radAnswer3.isChecked()){
                    answerGiven = radAnswer3.getText().toString();
                    selectAnswer();
                }
                else if (radAnswer4.isChecked()){
                    answerGiven = radAnswer4.getText().toString();
                    selectAnswer();
                }
                else {
                    Snackbar.make(view, "Select an answer", BaseTransientBottomBar.LENGTH_SHORT).show();
                }
            }
        });

        // goes to the previous question
        btnPreviousQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentQuestionId--;
                displayCurrentQuestion();
                radAnswers.clearCheck();
                //TODO: checks the correct radio button for answer previously given
                if (radAnswer1.getText().equals(currentQuestion.getAnswerGiven())) {
                    Log.d("Radio Button", "1");
                    radAnswer1.setChecked(true);
                }
                else if (radAnswer2.getText().equals(currentQuestion.getAnswerGiven())) {
                    Log.d("Radio Button", "2");
                    radAnswer2.setChecked(true);
                }
                else if (radAnswer3.getText().equals(currentQuestion.getAnswerGiven())) {
                    Log.d("Radio Button", "3");
                    radAnswer3.setChecked(true);
                }
                else if (radAnswer4.getText().equals(currentQuestion.getAnswerGiven())) {
                    Log.d("Radio Button", "4");
                    radAnswer4.setChecked(true);
                }
            }
        });
    }

    private void displayCurrentQuestion() {
        ActionBar actionBar = getSupportActionBar();
        // sets action bar title to the question number
        if (actionBar != null) {
            actionBar.setTitle("Question #" + (currentQuestionId + 1));
        }

        // disables btnPreviousQuestion if it is the first question
        if (currentQuestionId == 0) {
            btnPreviousQuestion.setEnabled(false);
        }
        else {
            btnPreviousQuestion.setEnabled(true);
        }

        if (currentQuestionId + 1 == questions.size()){
            btnNextQuestion.setText("Finish Quiz");
        }
        else {
            btnNextQuestion.setText("Next Question");
        }

        currentQuestion = questions.get(currentQuestionId);
        txtQuestionCategory.setText("Category: " + currentQuestion.getCategory());
        txtQuestion.setText(currentQuestion.getQuestionString());

        List<String> answers = currentQuestion.getIncorrectAnswers();
        answers.add(currentQuestion.getCorrectAnswer());

        // if the seconds answer in answers is "", then it is a true/false. so remove the second and third incorrect answers from answers
        if (answers.get(1).equals("")){
            answers.remove(1);
            answers.remove(1);
        }

        // puts answers in random radio buttons
        Random random = new Random();

        int numberOfAnswers = answers.size();

        int firstAnswer = random.nextInt(numberOfAnswers);
        radAnswer1.setText(answers.get(firstAnswer));
        answers.remove(firstAnswer);

        int secondAnswer = random.nextInt(numberOfAnswers - 1);
        radAnswer2.setText(answers.get(secondAnswer));
        answers.remove(secondAnswer);

        if (numberOfAnswers > 2) {
            int thirdAnswer = random.nextInt(numberOfAnswers - 2);
            radAnswer3.setText(answers.get(thirdAnswer));
            answers.remove(thirdAnswer);

            radAnswer4.setText(answers.get(0));

            radAnswer3.setVisibility(View.VISIBLE);
            radAnswer4.setVisibility(View.VISIBLE);
        }
        else{
            radAnswer3.setVisibility(View.GONE);
            radAnswer4.setVisibility(View.GONE);
        }
    }

    private void selectAnswer() {
        try {
            ((TriviaDB) getApplicationContext()).addQuestionAnswerGiven(answerGiven, currentQuestion.getQuestionId());
        }
        catch (Exception e) {
            // no-op
        }

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
}