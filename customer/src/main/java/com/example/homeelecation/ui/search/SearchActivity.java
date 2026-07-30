package com.example.homeelecation.ui.search;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeelecation.R;
import com.example.homeelecation.ui.wishList.WishlistAdapter;
import com.example.homeelecation.ui.wishList.WishlistModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchActivity extends AppCompatActivity {

    private TextView noResultText;
    private RecyclerView resultsRecycler, recentRecycler;
    private SearchView searchView;
    private LinearLayout recentContainer;
    private ImageView backBtn;
    private TextView clearAllRecent;

    private List<WishlistModel> resultsList = new ArrayList<>();
    private Adapter resultsAdapter;
    private RecentSearchAdapter recentAdapter;
    private List<String> recentQueries = new ArrayList<>();

    private static final String PREFS_NAME = "HomeElecationSearch";
    private static final String KEY_RECENT = "recent_queries";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Init Views
        noResultText = findViewById(R.id.search_activity_text_View);
        resultsRecycler = findViewById(R.id.search_activity_recycler_view);
        recentRecycler = findViewById(R.id.recent_search_recycler);
        searchView = findViewById(R.id.search_activity_search_view);
        recentContainer = findViewById(R.id.recent_search_container);
        backBtn = findViewById(R.id.search_back_btn);
        clearAllRecent = findViewById(R.id.clear_all_recent);

        // Add Tooltip (Description on Long Press)
        androidx.appcompat.widget.TooltipCompat.setTooltipText(backBtn, "Back");

        // Ensure cursor is visible with a slight delay
        searchView.postDelayed(() -> {
            EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
            if (searchEditText != null) {
                searchEditText.requestFocus();
                searchEditText.setCursorVisible(true);
                searchEditText.setTextColor(Color.BLACK);
                searchEditText.setHintTextColor(Color.GRAY);
            }
        }, 200);

        backBtn.setOnClickListener(v -> finish());

        // Setup Result Adapter
        resultsRecycler.setLayoutManager(new LinearLayoutManager(this));
        resultsAdapter = new Adapter(resultsList, false);
        resultsAdapter.setFormSearch(true);
        resultsRecycler.setAdapter(resultsAdapter);

        // Setup Recent Adapter
        loadRecentQueries();
        recentRecycler.setLayoutManager(new LinearLayoutManager(this));
        recentAdapter = new RecentSearchAdapter(recentQueries, query -> {
            searchView.setQuery(query, true);
        });
        recentRecycler.setAdapter(recentAdapter);

        clearAllRecent.setOnClickListener(v -> {
            recentQueries.clear();
            saveRecentQueries();
            updateUI(true);
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                addToRecent(query);
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    updateUI(true);
                } else if (newText.length() > 2) {
                    performSearch(newText);
                }
                return true;
            }
        });

        updateUI(true);
    }

    private void updateUI(boolean showRecent) {
        if (showRecent) {
            recentContainer.setVisibility(recentQueries.isEmpty() ? View.GONE : View.VISIBLE);
            resultsRecycler.setVisibility(View.GONE);
            noResultText.setVisibility(View.GONE);
            recentAdapter.notifyDataSetChanged();
        } else {
            recentContainer.setVisibility(View.GONE);
            resultsRecycler.setVisibility(View.VISIBLE);
        }
    }

    private void loadRecentQueries() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String saved = prefs.getString(KEY_RECENT, "");
        recentQueries.clear();
        if (!saved.isEmpty()) {
            recentQueries.addAll(Arrays.asList(saved.split("\\|")));
        }
    }

    private void saveRecentQueries() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < recentQueries.size(); i++) {
            sb.append(recentQueries.get(i));
            if (i < recentQueries.size() - 1) sb.append("|");
        }
        prefs.edit().putString(KEY_RECENT, sb.toString()).apply();
    }

    private void addToRecent(String query) {
        query = query.trim();
        if (query.isEmpty()) return;
        recentQueries.remove(query); // Remove if exists to move to top
        recentQueries.add(0, query);
        if (recentQueries.size() > 10) recentQueries.remove(10); // Limit history
        saveRecentQueries();
    }

    private void performSearch(String query) {
        updateUI(false);
        final String searchStr = query.toLowerCase().trim();
        FirebaseFirestore.getInstance().collection("Product_Details")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        resultsList.clear();
                        for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                            String title = doc.getString("productTitle").toLowerCase();
                            List<String> tags = (List<String>) doc.get("tags");
                            
                            boolean match = title.contains(searchStr);
                            if (!match && tags != null) {
                                for (String tag : tags) {
                                    if (tag.toLowerCase().contains(searchStr)) {
                                        match = true;
                                        break;
                                    }
                                }
                            }

                            if (match) {
                                List<String> images = (List<String>) doc.get("imageUrls");
                                String displayImage = (images != null && !images.isEmpty()) ? images.get(0) : "";
                                WishlistModel model = new WishlistModel(doc.getId(), displayImage,
                                        doc.getLong("freeCoupon"),
                                        Double.parseDouble(doc.get("starRating").toString()),
                                        doc.getLong("totalRatings"),
                                        doc.getString("productTitle"),
                                        doc.getLong("productPrise"),
                                        doc.getLong("productCatPrise"),
                                        doc.getString("paymentMethod"));
                                resultsList.add(model);
                            }
                        }

                        if (resultsList.isEmpty()) {
                            noResultText.setVisibility(View.VISIBLE);
                            resultsRecycler.setVisibility(View.GONE);
                            noResultText.setText("No products found for \"" + query + "\"");
                        } else {
                            noResultText.setVisibility(View.GONE);
                            resultsRecycler.setVisibility(View.VISIBLE);
                            resultsAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(SearchActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // --- Inner Adapters ---

    private class RecentSearchAdapter extends RecyclerView.Adapter<RecentSearchAdapter.ViewHolder> {
        private List<String> queries;
        private OnRecentClickListener listener;

        public RecentSearchAdapter(List<String> queries, OnRecentClickListener listener) {
            this.queries = queries;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_search_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String q = queries.get(position);
            holder.text.setText(q);
            holder.itemView.setOnClickListener(v -> listener.onRecentClick(q));
            holder.delete.setOnClickListener(v -> {
                queries.remove(position);
                saveRecentQueries();
                notifyDataSetChanged();
                if (queries.isEmpty()) updateUI(true);
            });
        }

        @Override
        public int getItemCount() { return queries.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView text;
            ImageView delete;
            ViewHolder(View v) {
                super(v);
                text = v.findViewById(R.id.recent_query_text);
                delete = v.findViewById(R.id.delete_recent_query);
            }
        }
    }

    interface OnRecentClickListener {
        void onRecentClick(String query);
    }

    class Adapter extends WishlistAdapter implements Filterable {
        public Adapter(List<WishlistModel> wishlistModelList, boolean wishlist) {
            super(wishlistModelList, wishlist);
        }
        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();
                    results.values = resultsList;
                    results.count = resultsList.size();
                    return results;
                }
                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    notifyDataSetChanged();
                }
            };
        }
    }
}
