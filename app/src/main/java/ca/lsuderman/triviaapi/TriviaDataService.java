package ca.lsuderman.triviaapi;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TriviaDataService {

    Context context;


    public TriviaDataService(Context context){
        this.context = context;
    }

    public interface QuestionsResponse {
        void onError(String error);
        void onResponse(List<Question> questions);
    }
    public void getQuestions(QuestionsResponse questionsResponse){
         List<Question> questions = new ArrayList<>();

         String url = "https://opentdb.com/api.php?amount=3";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray results = response.getJSONArray("results");

                    for (int i = 0; i < results.length(); i++) {
                        JSONObject questionFromAPI = (JSONObject) results.get(i);
                        Question question = new Question();

                        question.setCategory(questionFromAPI.getString("category"));
                        question.setType(questionFromAPI.getString("type"));
                        question.setDifficulty(questionFromAPI.getString("difficulty"));
                        question.setQuestionString(questionFromAPI.getString("question"));
                        question.setCorrectAnswer(questionFromAPI.getString("correct_answer"));

                        JSONArray incorrectAnswersJson = questionFromAPI.getJSONArray("incorrect_answers");

                        List<String> incorrectAnswers = new ArrayList<>();
                        for (int j = 0; j < incorrectAnswersJson.length(); j++) {
                            incorrectAnswers.add(incorrectAnswersJson.getString(j));
                        }

                        question.setIncorrectAnswers(incorrectAnswers);

                        questions.add(question);
                    }
                    questionsResponse.onResponse(questions);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                questionsResponse.onError("Something went wrong :(");
            }
        });
        MySingleton.getInstance(context).addToRequestQueue(request);
    }
}
