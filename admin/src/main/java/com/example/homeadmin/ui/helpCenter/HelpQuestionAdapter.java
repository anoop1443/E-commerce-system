package com.example.homeadmin.ui.helpCenter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeadmin.R;

import java.util.List;

public class HelpQuestionAdapter extends RecyclerView.Adapter<HelpQuestionAdapter.ViewHolder> {

    private List<HelpQuestionModel> questionList;
    private OnQuestionClickListener listener;

    public interface OnQuestionClickListener {
        void onEditClick(HelpQuestionModel question);
        void onDeleteClick(HelpQuestionModel question);
    }

    public HelpQuestionAdapter(List<HelpQuestionModel> questionList, OnQuestionClickListener listener) {
        this.questionList = questionList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_help_question, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HelpQuestionModel question = questionList.get(position);
        holder.bind(question, listener);
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView questionView, answerView;
        View editBtn, deleteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            questionView = itemView.findViewById(R.id.text_question);
            answerView = itemView.findViewById(R.id.text_answer);
            editBtn = itemView.findViewById(R.id.btn_edit_question);
            deleteBtn = itemView.findViewById(R.id.btn_delete_question);
        }

        public void bind(final HelpQuestionModel question, final OnQuestionClickListener listener) {
            questionView.setText(question.getQuestion());
            answerView.setText(question.getAnswer());

            editBtn.setOnClickListener(v -> listener.onEditClick(question));
            deleteBtn.setOnClickListener(v -> listener.onDeleteClick(question));
        }
    }
}
