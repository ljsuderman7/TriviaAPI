package ca.lsuderman.triviaapi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ViewQuizzesActivity extends AppCompatActivity {

    private LinearLayout llQuizzes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_quizzes);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Previous Quizzes");
        }

        llQuizzes = findViewById(R.id.llQuizzes);

        int totalQuizzes = 0;

        try {
//            questions = ((TriviaDB) getApplicationContext()).getAllQuestions();
            totalQuizzes = ((TriviaDB) getApplicationContext()).getNextQuizId() - 1;
        } catch (Exception exception) {
            // no-op
        }

        for (int i = 1; i <= totalQuizzes; i++) {
            int quizId = i;
            LinearLayout quizLayout = new LinearLayout(ViewQuizzesActivity.this);
            LinearLayout detailsLayout = new LinearLayout(ViewQuizzesActivity.this);
            TextView txtDetails = new TextView(ViewQuizzesActivity.this);
            Button btnRetake = new Button(ViewQuizzesActivity.this);

            detailsLayout.setOrientation(LinearLayout.HORIZONTAL);
            quizLayout.setOrientation(LinearLayout.VERTICAL);

            btnRetake.setText("Retake Test");
            btnRetake.setEnabled(true);

            btnRetake.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), QuestionActivity.class);
                    intent.putExtra("quizId", quizId);
                    startActivity(intent);
                }
            });

            List<Question> quiz = new ArrayList<>();

            try {
                quiz = ((TriviaDB) getApplicationContext()).getAllQuestionsByQuizId(i);
            } catch (Exception exception) {
                // no-op
            }

            int correct = 0;
            for (Question question: quiz){
                if (question.getAnswerGiven().equals(question.getCorrectAnswer())) {
                    correct++;
                }
            }

            txtDetails.setText("Result: " + correct + "/" + quiz.size());

            quizLayout.addView(txtDetails);
            quizLayout.addView(btnRetake);

            quizLayout.addView(detailsLayout);
            llQuizzes.addView(quizLayout);
        }
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