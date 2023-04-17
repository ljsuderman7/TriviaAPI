package ca.lsuderman.triviaapi;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.List;

public class QuizInfoAdapter extends RecyclerView.Adapter<QuizInfoAdapter.viewHolder> {
    private final List<Quiz> quizzes;
    private static final DecimalFormat df = new DecimalFormat("0.00");
    private static int quizId = 0;

    public QuizInfoAdapter(List<Quiz> quizzes) {
        this.quizzes = quizzes;
    }


    @NonNull
    @Override
    public QuizInfoAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quiz_info_layout, parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizInfoAdapter.viewHolder holder, int position) {
        Quiz quiz = quizzes.get(position);
        quizId = quiz.getId();
        holder.txtQuizId.setText(String.valueOf(quiz.getId()));
        holder.txtQuizCategory.setText(quiz.getCategory());
        holder.txtQuizDifficulty.setText(quiz.getDifficulty());
        holder.txtQuizQuestionType.setText(quiz.getQuestionType());

        double latestQuizAverage = (double) quiz.getCorrectAnswers() / (double) quiz.getNumberOfQuestions() * 100;
        holder.txtQuizScore.setText(String.valueOf(quiz.getCorrectAnswers()) + "/" + String.valueOf(quiz.getNumberOfQuestions()) + " (" + df.format(latestQuizAverage) + "%)");

    }

    @Override
    public int getItemCount() {
        return quizzes.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        private final TextView txtQuizId;
        private final TextView txtQuizCategory;
        private final TextView txtQuizDifficulty;
        private final TextView txtQuizQuestionType;
        private final TextView txtQuizScore;
        private final Button btnRetakeQuiz;
        private final Button btnDeleteQuiz;

        public viewHolder(@NonNull View itemView){
            super(itemView);
            txtQuizId = itemView.findViewById(R.id.txtQuizId);
            txtQuizCategory = itemView.findViewById(R.id.txtQuizCategory);
            txtQuizDifficulty = itemView.findViewById(R.id.txtQuizDifficulty);
            txtQuizQuestionType = itemView.findViewById(R.id.txtQuizQuestionType);
            txtQuizScore = itemView.findViewById(R.id.txtQuizScore);

            btnRetakeQuiz = itemView.findViewById(R.id.btnRetakeQuiz);
            btnRetakeQuiz.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), QuestionActivity.class);
                    intent.putExtra("quizId", quizId);
                    intent.putExtra("singleDelete", false);
                    view.getContext().startActivity(intent);
                }
            });

            btnDeleteQuiz = itemView.findViewById(R.id.btnDeleteQuiz);
            btnDeleteQuiz.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), ViewQuizzesActivity.class);
                    intent.putExtra("quizId", quizId);
                    intent.putExtra("singleDelete", true);
                    view.getContext().startActivity(intent);
//                    try {
//                        ((TriviaDB) view.getContext()).deleteQuiz(quizId);
//                    } catch (Exception ex) {
//                        ex.printStackTrace();
//                    }
                }
            });
        }
    }
}
