package ca.lsuderman.triviaapi;

import android.content.Context;
import android.util.Log;
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

    //region Get Questions

    public interface QuestionsResponse {
        void onError(String error);
        void onResponse(List<Question> questions);
    }
    public void getQuestions(String amount, String category, String difficulty, String type, QuestionsResponse questionsResponse){
        List<Question> questions = new ArrayList<>();

        String url = "https://opentdb.com/api.php?amount=" + amount;
        if (!category.equals("")){
            url += "&category=" + category;
        }

        if (!difficulty.equals("")){
            url += "&difficulty=" + difficulty;
        }

        if (!type.equals("")){
            url += "&type=" + type;
        }

        Log.d("amount", amount);
        Log.d("category", category);
        Log.d("difficulty", difficulty);
        Log.d("type", type);
        Log.d("URL", url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray results = response.getJSONArray("results");

                    Log.d("Results", String.valueOf(results.length()));

                    for (int i = 0; i < results.length(); i++) {
                        JSONObject questionFromAPI = (JSONObject) results.get(i);
                        Question question = new Question();

                        question.setCategory(questionFromAPI.getString("category"));
                        question.setType(questionFromAPI.getString("type"));
                        question.setDifficulty(questionFromAPI.getString("difficulty"));
                        question.setQuestionString(questionFromAPI.getString("question").replace("&quot;", "\"")
                                                                                                .replace("&amp;", "&")
                                                                                                .replace("#039;", "\'"));
                        question.setCorrectAnswer(questionFromAPI.getString("correct_answer").replace("&quot;", "\"")
                                                                                                    .replace("&amp;", "&")
                                                                                                    .replace("#039;", "\'"));

                        JSONArray incorrectAnswersJson = questionFromAPI.getJSONArray("incorrect_answers");
                        List<String> incorrectAnswers = new ArrayList<>();
                        for (int j = 0; j < incorrectAnswersJson.length(); j++) {
                            incorrectAnswers.add(incorrectAnswersJson.getString(j).replace("&quot;", "\"")
                                                                                    .replace("&amp;", "&")
                                                                                    .replace("#039;", "\'"));
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

    //endregion

    //region Get All Categories

    public interface CategoriesResponse {
        void onError(String error);
        void onResponse(List<Category> categories);
    }

    public void getCategories(CategoriesResponse categoriesResponse){
        List<Category> categories = new ArrayList<>();

        String url = "https://opentdb.com/api_category.php";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray triviaCategories = response.getJSONArray("trivia_categories");

                    for (int i = 0; i < triviaCategories.length(); i++) {
                        JSONObject categoryFromAPI = (JSONObject) triviaCategories.get(i);

                        Category category = new Category();
                        category.setId(categoryFromAPI.getInt("id"));
                        category.setName(categoryFromAPI.getString("name"));
                        categories.add(category);
                    }
                    categoriesResponse.onResponse(categories);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                categoriesResponse.onError("Something went wrong :(");
            }
        });
        MySingleton.getInstance(context).addToRequestQueue(request);
    }

    //endregion

    //region Get Chosen Category ID

    public interface CategoryIdResponse {
        void onError(String error);
        void onResponse(String id);
    }

    public void getCategoryId(String categoryName, CategoryIdResponse categoryIdResponse){
        String url = "https://opentdb.com/api_category.php";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray triviaCategories = response.getJSONArray("trivia_categories");
                     String categoryId = "";

                    for (int i = 0; i < triviaCategories.length(); i++) {
                        JSONObject categoryFromAPI = (JSONObject) triviaCategories.get(i);
                        if (categoryName.equals(categoryFromAPI.getString("name"))){
                            categoryId = String.valueOf(categoryFromAPI.getInt("id"));
                        }
                    }
                    categoryIdResponse.onResponse(categoryId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                categoryIdResponse.onError("Something went wrong :(");
            }
        });
        MySingleton.getInstance(context).addToRequestQueue(request);
    }

    //endregion

    //TODO: check that there is enough questions in selected category
    //https://opentdb.com/api_count.php?category=CATEGORY_ID_HERE

    //https://opentdb.com/api_count_global.php
}
