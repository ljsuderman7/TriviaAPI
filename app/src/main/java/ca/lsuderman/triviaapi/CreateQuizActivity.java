package ca.lsuderman.triviaapi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class CreateQuizActivity extends AppCompatActivity {

    String[] categoryOptions;
    String categoryId = "";
    private Button btnStartQuiz;
    private TextView txtNumberOfQuestions;
    private AutoCompleteTextView txtCategory, txtQuestionType, txtDifficulty;
    private ListView lvQuizItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quiz);

        btnStartQuiz = findViewById(R.id.btnStartQuiz);
        txtNumberOfQuestions = findViewById(R.id.txtNumberOfQuestions);
        txtCategory = findViewById(R.id.txtCategory);
        txtQuestionType = findViewById(R.id.txtQuestionType);
        txtDifficulty = findViewById(R.id.txtDifficulty);
        lvQuizItems = findViewById(R.id.lvQuizItems);

        // Get all the categories
        TriviaDataService triviaDataService = new TriviaDataService(CreateQuizActivity.this);
        triviaDataService.getCategories(new TriviaDataService.CategoriesResponse() {
            @Override
            public void onError(String error) {
                Toast.makeText(CreateQuizActivity.this, error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(List<Category> categories) {
                // TODO: Add "Any" to start of list without it taking a few clicks to load
                categoryOptions = new String[categories.size()]; // + 1
                //categoryOptions[0] = "Any";
                for (int i = 0; i < categories.size(); i++) {
                    categoryOptions[i] = categories.get(i).getName();
                }
                // Add all of the options to txtCategory
                ArrayAdapter categoryAdapter = new ArrayAdapter(CreateQuizActivity.this, R.layout.list_item, categoryOptions);
                txtCategory.setAdapter(categoryAdapter);
            }
        });

        String[] difficultyOptions = {"Any", "Easy", "Medium", "Hard"};
        ArrayAdapter<String> difficultyAdapter = new ArrayAdapter<String>(CreateQuizActivity.this, R.layout.list_item, difficultyOptions);
        txtDifficulty.setAdapter(difficultyAdapter);

        String[] questionTypeOptions = {"Any", "Multiple Choice", "True/False"};
        ArrayAdapter<String> questionTypeAdapter = new ArrayAdapter<String>(CreateQuizActivity.this, R.layout.list_item, questionTypeOptions);
        txtQuestionType.setAdapter(questionTypeAdapter);

        btnStartQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amount = txtNumberOfQuestions.getText().toString();
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
                    }
                });

                //TODO: check that selected category has enough questions

                // saves questionType as correct values for api submission
                String questionType = "";
                if (txtQuestionType.getText().toString().equals("Multiple Choice")) {
                    questionType = "multiple";
                }
                else if (txtQuestionType.getText().toString().equals("True/False")) {
                    questionType = "boolean";
                }

                // saves difficulty as correct values for api submission
                String difficulty = "";
                if (txtDifficulty.getText().toString().equals("Easy")) {
                    difficulty = "easy";
                }
                else if (txtQuestionType.getText().toString().equals("Medium")) {
                    difficulty = "medium";
                }
                else if (txtQuestionType.getText().toString().equals("hard")) {
                    difficulty = "hard";
                }

                triviaDataService.getQuestions(amount, categoryId, difficulty, questionType, new TriviaDataService.QuestionsResponse() {
                    @Override
                    public void onError(String error) {
                        Toast.makeText(CreateQuizActivity.this, error, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(List<Question> questions) {
                        // TODO: Save Questions


                        //startActivity(new Intent(getApplicationContext(), QuestionActivity.class));

                        ArrayAdapter arrayAdapter = new ArrayAdapter(CreateQuizActivity.this, android.R.layout.simple_list_item_1, questions);
                        lvQuizItems.setAdapter(arrayAdapter);
                    }
                });
            }
        });
    }
}