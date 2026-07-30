package com.example.homeadmin.ui.search;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeadmin.R;
import com.example.homeadmin.ui.wishList.WishlistAdapter;
import com.example.homeadmin.ui.wishList.WishlistModel;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchActivity extends AppCompatActivity {

    private TextView noResultsTextView;
    private RecyclerView searchRecyclerView;
    private SearchView searchView;
    private WishlistAdapter adapter;
    private final List<WishlistModel> searchResultsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        noResultsTextView = findViewById(R.id.search_activity_text_View);
        searchRecyclerView = findViewById(R.id.search_activity_recycler_view);
        searchView = findViewById(R.id.search_activity_search_view);

        // --- Setup RecyclerView ---
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Use the standard WishlistAdapter. It does not need a custom Filterable implementation.
        adapter = new WishlistAdapter(searchResultsList, false);
        adapter.setFormSearch(true); // Your custom flag from previous code
        searchRecyclerView.setAdapter(adapter);

        // --- Setup SearchView ---
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query == null || query.trim().isEmpty()) {
                    Toast.makeText(SearchActivity.this, "Please enter a search term", Toast.LENGTH_SHORT).show();
                    return false;
                }

                // Clear previous results and prepare UI for new search
                searchResultsList.clear();
                adapter.notifyDataSetChanged();
                noResultsTextView.setVisibility(View.GONE);
                searchRecyclerView.setVisibility(View.VISIBLE);

                performSearch(query);
                return true; // We have handled the search
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Not implementing live search for now to keep it simple
                return false;
            }
        });
    }

    private void performSearch(String query) {
        // Split the query into individual tags, converted to lowercase
        String[] tags = query.toLowerCase().split(" ");
        List<Task<QuerySnapshot>> searchTasks = new ArrayList<>();

        // Create a Firestore query task for each tag
        for (String tag : tags) {
            if (!tag.trim().isEmpty()) {
                searchTasks.add(FirebaseFirestore.getInstance().collection("Product_Details")
                        .whereArrayContains("tags", tag.trim())
                        .get());
            }
        }

        if (searchTasks.isEmpty()) {
            return; // Nothing to search
        }

        // Use Tasks.whenAllSuccess to wait for all parallel queries to complete
        Tasks.whenAllSuccess(searchTasks).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Use a Set to automatically handle duplicate product IDs from different tag searches
                Set<String> foundProductIds = new HashSet<>();

                List<?> querySnapshots = task.getResult();
                for (Object snapshotObject : querySnapshots) {
                    QuerySnapshot querySnapshot = (QuerySnapshot) snapshotObject;
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        // Only add the product if its ID hasn't been added already
                        if (foundProductIds.add(document.getId())) {
                            try {
                                // IMPORTANT: Corrected field names based on your AddProductActivity
                                WishlistModel model = new WishlistModel(document.getId(),
                                        ((List<String>) document.get("imageUrls")).get(0), // Correct: imageUrls is a List
                                        document.getLong("freeCoupon"),
                                        document.getDouble("starRating"), // Correct: starRating is a double
                                        document.getLong("totalRatings"),
                                        document.getString("productTitle"),
                                        document.getLong("productPrise"),
                                        document.getLong("productCatPrise"),
                                        document.getString("paymentMethod")
                                );
                                searchResultsList.add(model);
                            } catch (Exception e) {
                                // Log error if a product fails to parse, but don't crash the app
                                Log.e("SearchActivity", "Failed to parse product " + document.getId(), e);
                            }
                        }
                    }
                }

                // After all tasks are done and results are combined, update the UI
                if (searchResultsList.isEmpty()) {
                    noResultsTextView.setText("No products found for '" + query + "'");
                    noResultsTextView.setVisibility(View.VISIBLE);
                    searchRecyclerView.setVisibility(View.GONE);
                } else {
                    adapter.notifyDataSetChanged();
                }

            } else {
                String error = task.getException() != null ? task.getException().getMessage() : "An unknown error occurred.";
                Toast.makeText(SearchActivity.this, "Search failed: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
