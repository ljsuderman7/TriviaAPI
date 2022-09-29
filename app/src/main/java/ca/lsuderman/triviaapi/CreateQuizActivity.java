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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

public class CreateQuizActivity extends AppCompatActivity {

    private String[] categoryOptions;
    private String categoryId = "", questionType = "", difficulty = "", amount = "";
    private Button btnStartQuiz;
    private TextView txtNumberOfQuestions;
    private AutoCompleteTextView txtCategory, txtQuestionType, txtDifficulty;
    private TriviaDataService triviaDataService;
    private TextInputLayout inlNumberOfQuestions;

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

        // Get all the categories from API
        triviaDataService = new TriviaDataService(CreateQuizActivity.this);
        triviaDataService.getCategories(new TriviaDataService.CategoriesResponse() {
            @Override
            public void onError(String error) {
                Toast.makeText(CreateQuizActivity.this, error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(List<Category> categories) {
                categoryOptions = new String[categories.size()];
                //categoryOptions[0] = "Any";
                for (int i = 0; i < categories.size(); i++) {
                    categoryOptions[i] = categories.get(i).getName();
                }
                // Add all of the options to txtCategory
                ArrayAdapter categoryAdapter = new ArrayAdapter(CreateQuizActivity.this, R.layout.list_item, categoryOptions);
                txtCategory.setAdapter(categoryAdapter);
            }
        });

        // add all difficulty options
        String[] difficultyOptions = {"Any", "Easy", "Medium", "Hard"};
        ArrayAdapter<String> difficultyAdapter = new ArrayAdapter<String>(CreateQuizActivity.this, R.layout.list_item, difficultyOptions);
        txtDifficulty.setAdapter(difficultyAdapter);

        // add all type options
        String[] questionTypeOptions = {"Any", "Multiple Choice", "True/False"};
        ArrayAdapter<String> questionTypeAdapter = new ArrayAdapter<String>(CreateQuizActivity.this, R.layout.list_item, questionTypeOptions);
        txtQuestionType.setAdapter(questionTypeAdapter);

        // click button to start quiz
        btnStartQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // gets the amount of questions
                amount = txtNumberOfQuestions.getText().toString();
                int amountInt = 0;

                if (!amount.equals("")){
                    amountInt = Integer.parseInt(amount);
                }

                inlNumberOfQuestions.setError(null);

                if (amount.equals("")) {
                    inlNumberOfQuestions.setError("Must have at least 1 question");
                }
                else if (amountInt > 50){
                    inlNumberOfQuestions.setError("Cannot have more than 50 questions");
                }
                else {
                    // saves questionType as correct values for api submission
                    if (txtQuestionType.getText().toString().equals("Multiple Choice")) {
                        questionType = "multiple";
                    } else if (txtQuestionType.getText().toString().equals("True/False")) {
                        questionType = "boolean";
                    }

                    // saves difficulty as correct values for api submission
                    if (txtDifficulty.getText().toString().equals("Easy")) {
                        difficulty = "easy";
                    } else if (txtQuestionType.getText().toString().equals("Medium")) {
                        difficulty = "medium";
                    } else if (txtQuestionType.getText().toString().equals("hard")) {
                        difficulty = "hard";
                    }

                    // Get the correct categoryId for selected category
                    String categoryName = txtCategory.getText().toString();
                    triviaDataService.getCategoryId(categoryName, new TriviaDataService.CategoryIdResponse() {
                        @Override
                        public void onError(String error) {
                            Toast.makeText(CreateQuizActivity.this, error, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onResponse(String id) {
                            categoryId = id;
                            //TODO: check that selected category has enough questions
                            createQuiz();
                        }
                    });
                }
            }
        });
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
                    //no-op
                }

                for (Question question: questions) {
                    try {
                        ((TriviaDB) getApplicationContext()).addQuestion(quizId, question.getCategory(), question.getType(), question.getDifficulty(), question.getQuestionString(),
                                question.getCorrectAnswer(), question.getIncorrectAnswers());
                    } catch (Exception ex) {
                        // no-op
                    }
                }

                // Starts the quiz
                Intent intent = new Intent(getApplicationContext(), QuestionActivity.class);
                intent.putExtra("quizId", quizId);
                startActivity(intent);
            }
        });
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