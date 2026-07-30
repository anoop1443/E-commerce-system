package com.example.homeelecation.ui.profile;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeelecation.R;

import java.util.List;

public class HelpQuestionAdapter extends RecyclerView.Adapter<HelpQuestionAdapter.ViewHolder> {

    private List<HelpQuestionModel> questionList;

    public HelpQuestionAdapter(List<HelpQuestionModel> questionList) {
        this.questionList = questionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_help_question, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HelpQuestionModel model = questionList.get(position);
        holder.bind(model);
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView questionText, answerText;
        private ImageView arrowIcon;
        private View divider;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            questionText = itemView.findViewById(R.id.question_text);
            answerText = itemView.findViewById(R.id.answer_text);
            arrowIcon = itemView.findViewById(R.id.arrow_icon);
            divider = itemView.findViewById(R.id.divider);
        }

        public void bind(HelpQuestionModel model) {
            questionText.setText(model.getQuestion());
            answerText.setText(model.getAnswer());

            // Always hide locally since we are using new activity for "Full Page" feel
            answerText.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
            arrowIcon.setRotation(-90f); // Pointing to indicate "next page"

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(itemView.getContext(), FAQDetailActivity.class);
                intent.putExtra("QUESTION", model.getQuestion());
                intent.putExtra("ANSWER", model.getAnswer());
                itemView.getContext().startActivity(intent);
            });
        }
    }
}
