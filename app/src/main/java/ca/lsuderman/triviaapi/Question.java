package ca.lsuderman.triviaapi;

import java.util.List;

public class Question {
    private String category;
    private String type;
    private String difficulty;
    private String questionString;
    private String correctAnswer;
    private List<String> incorrectAnswers;

    public Question(String category, String type, String difficulty, String questionString, String correctAnswer, List<String> incorrectAnswers) {
        this.category = category;
        this.type = type;
        this.difficulty = difficulty;
        this.questionString = questionString;
        this.correctAnswer = correctAnswer;
        this.incorrectAnswers = incorrectAnswers;
    }

    public Question() {
    }

    @Override
    public String toString() {
        String message = "Category: " + category + "\n" +
                        "Type: " + type + "\n" +
                        "Difficulty: " + difficulty + "\n" +
                        "Question: " + questionString + "\n" +
                        "Correct Answer: " + correctAnswer + "\n";

        for (int i = 0; i < incorrectAnswers.size(); i++) {
            message += "Incorrect Answer #" + (i+1) + ": " + incorrectAnswers.get(i) + "\n";
        }

        return message;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getQuestionString() {
        return questionString;
    }

    public void setQuestionString(String questionString) {
        this.questionString = questionString;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public List<String> getIncorrectAnswers() {
        return incorrectAnswers;
    }

    public void setIncorrectAnswers(List<String> incorrectAnswers) {
        this.incorrectAnswers = incorrectAnswers;
    }
}
