package ca.lsuderman.triviaapi;

import android.app.Application;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TriviaDB extends Application {
    private static final String DB_NAME = "db_trivia";
    private static int DB_VERSION = 1;

    private SQLiteOpenHelper helper;

    @Override
    public void onCreate() {
        helper = new SQLiteOpenHelper(this, DB_NAME, null, DB_VERSION) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                db.execSQL("CREATE TABLE IF NOT EXISTS tbl_question(" +
                        "question_id INTEGER PRIMARY KEY," +
                        "quiz_id INTEGER NOT NULL," +
                        "category TEXT NOT NULL," +
                        "type TEXT NOT NULL," +
                        "difficulty TEXT NOT NULL," +
                        "question_string TEXT NOT NULL," +
                        "correct_answer TEXT NOT NULL," +
                        "incorrect_answer_1 TEXT NOT NULL," +
                        "incorrect_answer_2 TEXT," +
                        "incorrect_answer_3 TEXT," +
                        "answer_given TEXT)");
            }

            @Override
            public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
                // no-op
            }
        };
        super.onCreate();
    }

    public void addQuestion(int quizId, String category, String type, String difficulty, String questionString, String correctAnswer, List<String> incorrectAnswers) {
        String incorrectAnswer1 = incorrectAnswers.get(0);
        String incorrectAnswer2 = incorrectAnswers.get(1);
        String incorrectAnswer3 = incorrectAnswers.get(2);

//        if (type.equals("multiple")){
//            incorrectAnswer2 = incorrectAnswers.get(1);
//            incorrectAnswer3 = incorrectAnswers.get(2);
//        }

        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("INSERT INTO tbl_question(quiz_id, category, type, difficulty, question_string, correct_answer, incorrect_answer_1, incorrect_answer_2, incorrect_answer_3) " +
                "VALUES ('" + quizId + "', '" + category + "', '" + type + "', '" + difficulty + "', '" + questionString + "', '" + correctAnswer + "', " +
                "'" + incorrectAnswer1 + "', '" + incorrectAnswer2 + "', '" + incorrectAnswer3 + "')");
    }

    public void addQuestionAnswerGiven(String answerGiven, int id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("UPDATE tbl_question SET answer_given = '" + answerGiven + "' WHERE question_id = " + id);
    }

    public int getNextQuizId(){
        int id = 1;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM tbl_question ORDER BY quiz_id DESC", null );
        if (cursor != null) {
            cursor.moveToFirst();
            id = cursor.getInt(1) + 1;
        }
        cursor.close();
        return id;
    }

    public Question getQuestion(int id) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM tbl_question WHERE question_id=" + id, null );

        if (cursor != null) {
            cursor.moveToFirst();
        }

        Question question = new Question();
        question.setQuestionId(cursor.getInt(0));
        question.setQuizId(cursor.getInt(1));
        question.setCategory(cursor.getString(2));
        question.setType(cursor.getString(3));
        question.setDifficulty(cursor.getString(4));
        question.setQuestionString(cursor.getString(5));
        question.setCorrectAnswer(cursor.getString(6));

        String incorrectAnswer1 = cursor.getString(7);
        String incorrectAnswer2 = cursor.getString(8);
        String incorrectAnswer3 = cursor.getString(9);

        List<String> incorrectAnswers = new ArrayList<>();

        incorrectAnswers.add(incorrectAnswer1);
        incorrectAnswers.add(incorrectAnswer2);
        incorrectAnswers.add(incorrectAnswer3);

        question.setIncorrectAnswers(incorrectAnswers);
        question.setAnswerGiven(cursor.getString(10));

        cursor.close();
        return question;
    }

    public List<Question> getAllQuestions() {
        SQLiteDatabase db = helper.getReadableDatabase();
        List<Question> questions = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM tbl_question ORDER BY question_id", null );

        if (cursor != null) {
            cursor.moveToFirst();
        }

        if (cursor.getCount() > 0) {
            while (cursor.getPosition() < cursor.getCount()) {
                Question question = new Question();
                question.setQuestionId(cursor.getInt(0));
                question.setQuizId(cursor.getInt(1));
                question.setCategory(cursor.getString(2));
                question.setType(cursor.getString(3));
                question.setDifficulty(cursor.getString(4));
                question.setQuestionString(cursor.getString(5));
                question.setCorrectAnswer(cursor.getString(6));

                String incorrectAnswer1 = cursor.getString(7);
                String incorrectAnswer2 = cursor.getString(8);
                String incorrectAnswer3 = cursor.getString(9);

                List<String> incorrectAnswers = new ArrayList<>();

                incorrectAnswers.add(incorrectAnswer1);
                incorrectAnswers.add(incorrectAnswer2);
                incorrectAnswers.add(incorrectAnswer3);

                question.setIncorrectAnswers(incorrectAnswers);
                question.setAnswerGiven(cursor.getString(10));

                questions.add(question);

                cursor.moveToNext();
            }
        }

        cursor.close();
        return questions;
    }

    public List<Question> getAllQuestionsByQuizId(int id) {
        SQLiteDatabase db = helper.getReadableDatabase();
        List<Question> questions = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM tbl_question WHERE quiz_id=" + id + " ORDER BY question_id", null );

        if (cursor != null) {
            cursor.moveToFirst();
        }

        if (cursor.getCount() > 0) {
            while (cursor.getPosition() < cursor.getCount()) {
                Question question = new Question();
                question.setQuestionId(cursor.getInt(0));
                question.setQuizId(cursor.getInt(1));
                question.setCategory(cursor.getString(2));
                question.setType(cursor.getString(3));
                question.setDifficulty(cursor.getString(4));
                question.setQuestionString(cursor.getString(5));
                question.setCorrectAnswer(cursor.getString(6));

                String incorrectAnswer1 = cursor.getString(7);
                String incorrectAnswer2 = cursor.getString(8);
                String incorrectAnswer3 = cursor.getString(9);

                List<String> incorrectAnswers = new ArrayList<>();

                incorrectAnswers.add(incorrectAnswer1);
                incorrectAnswers.add(incorrectAnswer2);
                incorrectAnswers.add(incorrectAnswer3);

                question.setIncorrectAnswers(incorrectAnswers);
                question.setAnswerGiven(cursor.getString(10));

                questions.add(question);

                cursor.moveToNext();
            }
        }

        cursor.close();
        return questions;
    }

    public void deleteQuestion(int id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("DELETE FROM tbl_question WHERE question_id=" + id);
    }

    public void deleteAllQuestions() {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("DELETE FROM tbl_question");
    }
}
