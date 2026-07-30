package com.example.homeadmin.ui.management;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeadmin.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserListFragment extends Fragment {

    private RecyclerView userRecyclerView;
    private UserManagementAdapter adapter;
    private List<StaffProfileModel> userList, fullList;
    private FirebaseFirestore db;
    private Spinner roleFilterSpinner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);

        roleFilterSpinner = view.findViewById(R.id.roleFilterSpinner);
        userRecyclerView = view.findViewById(R.id.userRecyclerView);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        userList = new ArrayList<>();
        fullList = new ArrayList<>();
        adapter = new UserManagementAdapter(userList);
        userRecyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        setupSpinner();

        return view;
    }

    private void setupSpinner() {
        String[] roles = {"Customers", "Electrician Boys", "Admins"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, roles);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleFilterSpinner.setAdapter(spinnerAdapter);

        roleFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadUsers(roles[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadUsers(String role) {
        userList.clear();
        fullList.clear();
        Query query;

        if (role.equals("Customers")) {
            query = db.collection("USER");
        } else if (role.equals("Electrician Boys")) {
            query = db.collection("delivery_boy");
        } else {
            query = db.collection("UsersAdmin");
        }

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    StaffProfileModel model = new StaffProfileModel();
                    model.setUid(document.getId());
                    model.setName(document.getString("Full Name"));
                    if (model.getName() == null) model.setName(document.getString("name"));
                    model.setMobile(document.getString("mobile"));
                    if (model.getMobile() == null) model.setMobile(document.getString("phone"));

                    String dbRole = document.getString("role");
                    if (dbRole == null && role.equals("Electrician Boys")) dbRole = "Delivery Boy";
                    model.setRole(dbRole);

                    model.setStatus(document.getString("status"));
                    model.setProfileImage(document.getString("profile image"));
                    userList.add(model);
                    fullList.add(model);
                }
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void filterByMobile(String query) {
        userList.clear();
        if (query == null || query.isEmpty()) {
            userList.addAll(fullList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (StaffProfileModel model : fullList) {
                if (model.getMobile() != null && model.getMobile().toLowerCase().contains(lowerCaseQuery)) {
                    userList.add(model);
                }
            }
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
