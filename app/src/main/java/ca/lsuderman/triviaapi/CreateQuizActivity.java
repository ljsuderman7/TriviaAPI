package ca.lsuderman.triviaapi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

public class CreateQuizActivity extends AppCompatActivity {

    private String[] categoryOptions;
    private String categoryId = "", questionType = "", difficulty = "", amount = "";
    private Button btnStartQuiz;
    private TextView txtNumberOfQuestions;
    private AutoCompleteTextView txtCategory, txtQuestionType, txtDifficulty;
    private TriviaDataService triviaDataService;
    private TextInputLayout inlNumberOfQuestions, inlCategory, inlQuestionType, inlDifficulty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quiz);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Create New Quiz");
        }

        btnStartQuiz = findViewById(R.id.btnStartQuiz);
        txtNumberOfQuestions = findViewById(R.id.txtNumberOfQuestions);
        txtCategory = findViewById(R.id.txtCategory);
        txtQuestionType = findViewById(R.id.txtQuestionType);
        txtDifficulty = findViewById(R.id.txtDifficulty);
        inlNumberOfQuestions = findViewById(R.id.inlNumberOfQuestions);
        inlCategory = findViewById(R.id.inlCategory);
        inlQuestionType = findViewById(R.id.inlQuestionType);
        inlDifficulty = findViewById(R.id.inlDifficulty);

        // get all the categories from API
        triviaDataService = new TriviaDataService(CreateQuizActivity.this);
        triviaDataService.getCategories(new TriviaDataService.CategoriesResponse() {
            @Override
            public void onError(String error) {
                Toast.makeText(CreateQuizActivity.this, error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(List<Category> categories) {
                categoryOptions = new String[categories.size()];
                for (int i = 0; i < categories.size(); i++) {
                    categoryOptions[i] = categories.get(i).getName();
                }
                // Add all category options
                ArrayAdapter categoryAdapter = new ArrayAdapter(CreateQuizActivity.this, R.layout.list_item, categoryOptions);
                txtCategory.setAdapter(categoryAdapter);
            }
        });

        // add all difficulty options
        String[] difficultyOptions = {"Any", "Easy", "Medium", "Hard"};
        ArrayAdapter<String> difficultyAdapter = new ArrayAdapter<String>(CreateQuizActivity.this, R.layout.list_item, difficultyOptions);
        txtDifficulty.setAdapter(difficultyAdapter);

        // add all question type options
        String[] questionTypeOptions = {"Any", "Multiple Choice", "True/False"};
        ArrayAdapter<String> questionTypeAdapter = new ArrayAdapter<String>(CreateQuizActivity.this, R.layout.list_item, questionTypeOptions);
        txtQuestionType.setAdapter(questionTypeAdapter);



        // click button to create quiz
        btnStartQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // checks that all the text fields have a selected value, and the amount of questions is between 1-50
                if (validateData()) {
                    String categoryName = txtCategory.getText().toString();

                    // Get the correct categoryId for selected category
                    triviaDataService.getCategoryId(categoryName, new TriviaDataService.CategoryIdResponse() {
                        @Override
                        public void onError(String error) {
                            Toast.makeText(CreateQuizActivity.this, error, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onResponse(String id) {
                            categoryId = id;

                            // if a category has been chosen, check to see if there are enough questions for the chosen difficulty
                            if (!id.equals("")) {
                                triviaDataService.isEnoughQuestions(amount, categoryId, difficulty, new TriviaDataService.EnoughQuestionsResponse() {
                                    @Override
                                    public void onError(String error) {
                                        Toast.makeText(CreateQuizActivity.this, error, Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onResponse(boolean enoughQuestions, int totNumberOfQuestions) {
                                        // if there are enough questions for the category and difficulty, than make the quiz.
                                        // if there isn't, than make a snackbar saying that there isn't enough questions
                                        if (enoughQuestions) {
                                            createQuiz();
                                        }
                                        else {
                                            Snackbar.make(view, "There are only " + totNumberOfQuestions + " available questions for \""
                                                    + categoryName + "\" category with \""
                                                    + difficulty + "\" difficulty", BaseTransientBottomBar.LENGTH_LONG)
                                                    .setTextMaxLines(3)
                                                    .setAction("Set to " + totNumberOfQuestions, new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    txtNumberOfQuestions.setText(String.valueOf(totNumberOfQuestions));
                                                }
                                            }).show();
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    // validates the data submitted
    private boolean validateData() {
        boolean valid = true;

        inlNumberOfQuestions.setError(null);
        inlCategory.setError(null);
        inlDifficulty.setError(null);
        inlQuestionType.setError(null);

        // gets the amount of questions
        amount = txtNumberOfQuestions.getText().toString();
        int amountInt = 0;

        if (!amount.equals("")){
            amountInt = Integer.parseInt(amount);
        }
        if (amount.equals("")) {
            inlNumberOfQuestions.setError("Must have at least 1 question");
            valid = false;
        }
        else if (amountInt > 50){
            inlNumberOfQuestions.setError("Cannot have more than 50 questions");
            valid = false;
        }

        //checks that a category has been selected
        if (txtCategory.getText().toString().equals("")){
            valid = false;
            inlCategory.setError("Must select Category");
        }

        // checks that a question type has been selected
        // saves questionType as correct values for api submission
        if (txtQuestionType.getText().toString().equals("Multiple Choice")) {
            questionType = "multiple";
        }
        else if (txtQuestionType.getText().toString().equals("True/False")) {
            questionType = "boolean";
        }
        else if (txtQuestionType.getText().toString().equals("Any")){
            questionType = "Any";
        }
        else {
            valid = false;
            inlQuestionType.setError("Must select Question Type");
        }

        // checks that a difficulty has been selected
        // saves difficulty as correct values for api submission
        if (txtDifficulty.getText().toString().equals("Easy")) {
            difficulty = "easy";
        }
        else if (txtDifficulty.getText().toString().equals("Medium")) {
            difficulty = "medium";
        }
        else if (txtDifficulty.getText().toString().equals("Hard")) {
            difficulty = "hard";
        }
        else if (txtDifficulty.getText().toString().equals("Any")) {
            difficulty = "Any";
        }
        else {
            valid = false;
            inlDifficulty.setError("Must select Difficulty");
        }

        return valid;
    }

    private void createQuiz(){
        triviaDataService.getQuestions(amount, categoryId, difficulty, questionType, new TriviaDataService.QuestionsResponse() {
            @Override
            public void onError(String error) {
                Toast.makeText(CreateQuizActivity.this, error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(List<Question> questions) {
                // Saves the questions returned from the API to DB
                int quizId = 1;
                try {
                    quizId = ((TriviaDB) getApplicationContext()).getNextQuizId();
                } catch (Exception ex) {
                    Log.e("Exception", ex.toString());
                }

                try {
                    ((TriviaDB) getApplicationContext()).addQuiz(quizId, txtCategory.getText().toString(), txtQuestionType.getText().toString(),
                            txtDifficulty.getText().toString(), Integer.parseInt(amount));
                } catch (Exception ex) {
                    Log.e("Exception", ex.toString());
                }

                // saves the questions returned from the API to the DB
                for (Question question: questions) {
                    try {
                        ((TriviaDB) getApplicationContext()).addQuestion(quizId, question.getCategory(), question.getType(), question.getDifficulty(), question.getQuestionString(),
                                question.getCorrectAnswer(), question.getIncorrectAnswers());
                    } catch (Exception ex) {
                        Log.e("Exception", ex.toString());
                    }
                }

                // Starts the quiz
                Intent intent = new Intent(getApplicationContext(), QuestionActivity.class);
                intent.putExtra("quizId", quizId);
                startActivity(intent);
            }
        });
    }

    // return button (back arrow)
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