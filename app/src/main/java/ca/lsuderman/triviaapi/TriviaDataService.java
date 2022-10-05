package ca.lsuderman.triviaapi;

import android.content.Context;
import android.util.Log;

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

        // default URL that just includes amount of questions
        String url = "https://opentdb.com/api.php?amount=" + amount;
        // if there is a category, add it to the url
        if (!category.equals("")){
            url += "&category=" + category;
        }

        //if difficulty isn't "Any", add it to the url
        if (!difficulty.equals("") && !difficulty.equals("Any")){
            url += "&difficulty=" + difficulty;
        }

        //if question typ isn't "Any", add it to the url
        if (!type.equals("") && !type.equals("Any")){
            url += "&type=" + type;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // retrieves all the questions returned from API
                    JSONArray results = response.getJSONArray("results");

                    // saves each individual question
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
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                questionsResponse.onError("Something went wrong :(");
            }
        });
        // queues request in the singleton
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

    //region Enough questions in category

    public interface EnoughQuestionsResponse {
        void onError(String error);
        void onResponse(boolean enoughQuestions, int numberOfQuestions);
    }

    public void isEnoughQuestions(String amount, String id, String difficulty, EnoughQuestionsResponse enoughQuestionsResponse){
        String url = "https://opentdb.com/api_count.php?category=" + id;
        int requestedNumberOfQuestions = Integer.parseInt(amount);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject categoryNumberOfQuestions = response.getJSONObject("category_question_count");

                    int totalNumberOfQuestions = 0;

                    if (difficulty.equals("easy")) {
                        totalNumberOfQuestions = categoryNumberOfQuestions.getInt("total_easy_question_count");
                    }
                    else if (difficulty.equals("medium")) {
                        totalNumberOfQuestions = categoryNumberOfQuestions.getInt("total_medium_question_count");
                    }
                    else if (difficulty.equals("hard")) {
                        totalNumberOfQuestions = categoryNumberOfQuestions.getInt("total_hard_question_count");
                    }
                    else {
                        totalNumberOfQuestions = categoryNumberOfQuestions.getInt("total_question_count");
                    }

                    Log.d("totalNumberOfQuestions", String.valueOf(totalNumberOfQuestions));
                    Log.d("requestedNumberOfQuestions", String.valueOf(requestedNumberOfQuestions));

                    if (totalNumberOfQuestions >= requestedNumberOfQuestions) {
                        enoughQuestionsResponse.onResponse(true, totalNumberOfQuestions);
                        Log.d("Enough Questions", "True");
                    }
                    else {
                        enoughQuestionsResponse.onResponse(false, totalNumberOfQuestions);
                        Log.d("Enough Questions", "False");
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                enoughQuestionsResponse.onError("Something went wrong");
            }
        });
        MySingleton.getInstance(context).addToRequestQueue(request);
    }

    //endregion

    //https://opentdb.com/api_count_global.php
}
