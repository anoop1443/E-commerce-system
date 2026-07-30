package com.example.homeelecation.ui.profile;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeelecation.R;
import com.example.homeelecation.util.EdgeToEdgeUtils;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HelpQuestionActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private List<HelpQuestionModel> questionList;
    private HelpQuestionAdapter adapter;
    private String categoryId, categoryName;

    @Inject
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_question);

        EdgeToEdge.enable(this);

        // Apply Insets
        EdgeToEdgeUtils.applyTopInset(findViewById(R.id.app_bar_q));


        categoryId = getIntent().getStringExtra("CATEGORY_ID");
        categoryName = getIntent().getStringExtra("CATEGORY_NAME");

        Toolbar toolbar = findViewById(R.id.toolbar_q);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(categoryName);
        }

        recyclerView = findViewById(R.id.questions_recycler);
        progressBar = findViewById(R.id.loading_progress);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        questionList = new ArrayList<>();
        adapter = new HelpQuestionAdapter(questionList);
        recyclerView.setAdapter(adapter);

        loadQuestions();
    }

    private void loadQuestions() {
        if (categoryId == null) return;

        progressBar.setVisibility(View.VISIBLE);
        db.collection("HELP_CENTER")
                .document(categoryId)
                .collection("QUESTIONS")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            HelpQuestionModel model = document.toObject(HelpQuestionModel.class);
                            questionList.add(model);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Failed to load questions", Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.GONE);
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
