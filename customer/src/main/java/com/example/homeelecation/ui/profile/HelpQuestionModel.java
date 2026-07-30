package com.example.homeelecation.ui.profile;

public class HelpQuestionModel {
    private String question;
    private String answer;
    private boolean isExpanded = false;

    public HelpQuestionModel() {
        // Required for Firebase
    }

    public HelpQuestionModel(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }
}
