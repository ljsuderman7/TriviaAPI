package ca.lsuderman.triviaapi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.List;
import java.util.Queue;

public class MainActivity extends AppCompatActivity {

    private Button btnGenerate;
    private ListView lvQuizItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnGenerate = findViewById(R.id.btnGenerate);
        lvQuizItems = findViewById(R.id.lvQuizItems);

        TriviaDataService triviaDataService = new TriviaDataService(MainActivity.this);

        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                triviaDataService.getQuestions(new TriviaDataService.QuestionsResponse() {
                    @Override
                    public void onError(String error) {
                        Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(List<Question> questions) {
                        ArrayAdapter arrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, questions);
                        lvQuizItems.setAdapter(arrayAdapter);

//                        for (Question question: questions){
//                            txtTest.setText(question.toString());
//                        }
                    }
                });
            }
        });
    }
}