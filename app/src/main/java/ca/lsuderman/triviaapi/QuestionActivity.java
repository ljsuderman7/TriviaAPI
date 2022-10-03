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
    private TextView txtQuestionCategory, txtQuestion, txtQuestionDifficulty;
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
        txtQuestionDifficulty = findViewById(R.id.txtQuestionDifficulty);

        // gets all the questions that were created for the selected quiz
        quizId = getIntent().getExtras().getInt("quizId");
        questions = ((TriviaDB) getApplicationContext()).getAllQuestionsByQuizId(quizId);

        // displays the first question
        displayCurrentQuestion();

        // goes to the next question, saves the selected answer
        btnNextQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (radAnswer1.isChecked()) {
                    // converts it back to HTML characters to save better in the DB
                    answerGiven = Utilities.convertBackToHTMLCharacters(radAnswer1.getText().toString());
                    selectAnswer();
                }
                else if (radAnswer2.isChecked()){
                    answerGiven = Utilities.convertBackToHTMLCharacters(radAnswer2.getText().toString());
                    selectAnswer();
                }
                else if (radAnswer3.isChecked()){
                    answerGiven = Utilities.convertBackToHTMLCharacters(radAnswer3.getText().toString());
                    selectAnswer();
                }
                else if (radAnswer4.isChecked()){
                    answerGiven = Utilities.convertBackToHTMLCharacters(radAnswer4.getText().toString());
                    selectAnswer();
                }
                else {
                    Snackbar.make(view, "Select an answer", BaseTransientBottomBar.LENGTH_SHORT).show();
                }
            }
        });

        // goes to the previous question, has the answer that was originally selected checked
        // TODO: fix issue with not showing all the choices
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

        // if it is the last question, than change the text txtNextQuestion to "Finish Quiz"
        if (currentQuestionId + 1 == questions.size()){
            btnNextQuestion.setText("Finish Quiz");
        }
        else {
            btnNextQuestion.setText("Next Question");
        }

        // gets the current question from the list of all questions in the quiz
        currentQuestion = questions.get(currentQuestionId);

        // updates the category
        txtQuestionCategory.setText("Category: " + currentQuestion.getCategory());

        // displays the question's difficulty
        txtQuestionDifficulty.setText("Difficulty: " + currentQuestion.getDifficulty());

        // displays the question
        txtQuestion.setText(Utilities.replaceHTMLCharacters(currentQuestion.getQuestionString()));

        // puts the incorrect and correct answers together to put into radio buttons
        List<String> answers = currentQuestion.getIncorrectAnswers();
        answers.add(currentQuestion.getCorrectAnswer());

        // if the seconds answer in answers is "", then it is a true/false. so remove the second and third incorrect answers from answers
        if (answers.get(1).equals("")){
            answers.remove(1);
            answers.remove(1);
        }

        // puts answers in random radio buttons
        Random random = new Random();

        // saves the amount of answers to check if true/false later
        int numberOfAnswers = answers.size();

        // picks a random answer from list of answers and puts it as the first option
        // removes answer from the list
        int firstAnswer = random.nextInt(numberOfAnswers);
        radAnswer1.setText(Utilities.replaceHTMLCharacters(answers.get(firstAnswer)));
        answers.remove(firstAnswer);

        // picks a random answer from list of answers and puts it as the first option
        // removes answer from the list
        int secondAnswer = random.nextInt(numberOfAnswers - 1);
        radAnswer2.setText(Utilities.replaceHTMLCharacters(answers.get(secondAnswer)));
        answers.remove(secondAnswer);

        // if the number of answers is greater than 2, then it is a multiple choice question
        // if it is a multiple choice question than put options in the 3rd and 4th radio buttons
        if (numberOfAnswers > 2) {
            int thirdAnswer = random.nextInt(numberOfAnswers - 2);
            radAnswer3.setText(Utilities.replaceHTMLCharacters(answers.get(thirdAnswer)));
            answers.remove(thirdAnswer);

            // just puts the last remaining answer in the last radio button
            radAnswer4.setText(Utilities.replaceHTMLCharacters(answers.get(0)));

            radAnswer3.setVisibility(View.VISIBLE);
            radAnswer4.setVisibility(View.VISIBLE);
        }
        // if it is a true/false question, remove the 3rd and 4th radio buttons from the screen
        else{
            radAnswer3.setVisibility(View.GONE);
            radAnswer4.setVisibility(View.GONE);
        }
    }

    private void selectAnswer() {
        try {
            ((TriviaDB) getApplicationContext()).addQuestionAnswerGiven(answerGiven, currentQuestion.getQuestionId());
        }
        catch (Exception exception) {
            Log.e("Exception", exception.toString());
        }

        currentQuestionId++;
        // checks if there is another question
        if (currentQuestionId < questions.size()){
            // displays next question
            displayCurrentQuestion();
            radAnswers.clearCheck();
        }
        // if it is the last question then go to QuizResultsActivity
        else {
            // display results
            Intent intent = new Intent(getApplicationContext(), QuizResultsActivity.class);
            intent.putExtra("quizId", quizId);
            startActivity(intent);
        }
    }
}