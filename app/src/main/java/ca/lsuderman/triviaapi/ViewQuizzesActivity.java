package ca.lsuderman.triviaapi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.divider.MaterialDivider;
import com.google.android.material.timepicker.TimeFormat;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ViewQuizzesActivity extends AppCompatActivity {

    private Button btnDeleteAllQuizzes;
    private static final DecimalFormat df = new DecimalFormat("0.00");
    private int quizId = 0;
    private boolean singleDelete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_quizzes);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Previous Quizzes");
        }

        btnDeleteAllQuizzes = findViewById(R.id.btnDeleteAllQuizzes);

        List<Quiz> quizzes = new ArrayList<>();
        try {
            quizId = getIntent().getExtras().getInt("quizId");
            singleDelete = getIntent().getExtras().getBoolean("singleDelete");
        } catch (Exception ex) {
            // no-op
        }

        if (singleDelete){
            List<Question> questionsToDelete = new ArrayList<>();
            try {
                questionsToDelete = ((TriviaDB) getApplicationContext()).getAllQuestionsByQuizId(quizId);
                ((TriviaDB) getApplicationContext()).deleteQuiz(quizId);
                for (Question question : questionsToDelete){
                    ((TriviaDB) getApplicationContext()).deleteQuestion(question.getQuestionId());
                }
            } catch (Exception ex) {
                // no-op
            }
        }

        try {
            quizzes = ((TriviaDB) getApplicationContext()).getAllQuizzes();
        } catch (Exception ex) {
            // no-op
        }

        if (quizzes.size() == 0) {
            btnDeleteAllQuizzes.setVisibility(View.INVISIBLE);
        }
        else {
            btnDeleteAllQuizzes.setVisibility(View.VISIBLE);
        }

        RecyclerView rvQuizInfo = findViewById(R.id.rvQuizzes);

        QuizInfoAdapter quizInfoAdapter = new QuizInfoAdapter(quizzes);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvQuizInfo.setLayoutManager(linearLayoutManager);
        rvQuizInfo.setAdapter(quizInfoAdapter);

        // delete all quizzes
        btnDeleteAllQuizzes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    ((TriviaDB) getApplicationContext()).deleteAllQuestions();
                    ((TriviaDB) getApplicationContext()).deleteAllQuizzes();
                } catch (Exception exception) {
                    Log.d("Exception", exception.toString());
                }

                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        boolean result = true;
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                break;
            default:
                result = super.onOptionsItemSelected(item);
                break;
        }
        return result;
    }
}