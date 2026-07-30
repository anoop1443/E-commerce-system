package com.example.homeadmin.ui.helpCenter;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeadmin.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HelpCategoryDetailActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HelpQuestionAdapter adapter;
    private List<HelpQuestionModel> questionList = new ArrayList<>();
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private String categoryId;
    private String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_category_detail);

        categoryId = getIntent().getStringExtra("categoryId");
        categoryName = getIntent().getStringExtra("categoryName");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(categoryName);
        }

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recycler_view_questions);
        progressBar = findViewById(R.id.progress_bar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HelpQuestionAdapter(questionList, new HelpQuestionAdapter.OnQuestionClickListener() {
            @Override
            public void onEditClick(HelpQuestionModel question) {
                showQuestionDialog(question);
            }

            @Override
            public void onDeleteClick(HelpQuestionModel question) {
                deleteQuestion(question);
            }
        });
        recyclerView.setAdapter(adapter);

        findViewById(R.id.fab_add_question).setOnClickListener(v -> showQuestionDialog(null));

        loadQuestions();
    }

    private void loadQuestions() {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("HELP_CENTER").document(categoryId)
                .collection("QUESTIONS")
                .addSnapshotListener((value, error) -> {
                    progressBar.setVisibility(View.GONE);
                    if (error != null) {
                        Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    questionList.clear();
                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            HelpQuestionModel question = doc.toObject(HelpQuestionModel.class);
                            question.setId(doc.getId());
                            questionList.add(question);
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void showQuestionDialog(HelpQuestionModel question) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_question);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView title = dialog.findViewById(R.id.dialog_title);
        TextInputEditText questionEdit = dialog.findViewById(R.id.edit_question);
        TextInputEditText answerEdit = dialog.findViewById(R.id.edit_answer);
        Button cancelBtn = dialog.findViewById(R.id.btn_cancel);
        Button saveBtn = dialog.findViewById(R.id.btn_save);

        if (question != null) {
            title.setText("Edit FAQ");
            questionEdit.setText(question.getQuestion());
            answerEdit.setText(question.getAnswer());
        }

        cancelBtn.setOnClickListener(v -> dialog.dismiss());

        saveBtn.setOnClickListener(v -> {
            String q = questionEdit.getText().toString().trim();
            String a = answerEdit.getText().toString().trim();

            if (TextUtils.isEmpty(q) || TextUtils.isEmpty(a)) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            saveBtn.setEnabled(false);
            saveQuestion(q, a, question, dialog);
        });

        dialog.show();
    }

    private void saveQuestion(String q, String a, HelpQuestionModel question, Dialog dialog) {
        Map<String, Object> map = new HashMap<>();
        map.put("question", q);
        map.put("answer", a);

        if (question == null) {
            db.collection("HELP_CENTER").document(categoryId)
                    .collection("QUESTIONS").add(map).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Question added", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            dialog.findViewById(R.id.btn_save).setEnabled(true);
                        }
                    });
        } else {
            db.collection("HELP_CENTER").document(categoryId)
                    .collection("QUESTIONS").document(question.getId()).update(map).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Question updated", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            dialog.findViewById(R.id.btn_save).setEnabled(true);
                        }
                    });
        }
    }

    private void deleteQuestion(HelpQuestionModel question) {
        db.collection("HELP_CENTER").document(categoryId)
                .collection("QUESTIONS").document(question.getId()).delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Question deleted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Error deleting question", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
