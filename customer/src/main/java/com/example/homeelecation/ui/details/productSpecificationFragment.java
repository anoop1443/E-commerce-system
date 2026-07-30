package com.example.homeelecation.ui.details;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeelecation.R;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link productSpecificationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class productSpecificationFragment extends Fragment {



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public productSpecificationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment productSpecificationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static productSpecificationFragment newInstance(String param1, String param2) {
        productSpecificationFragment fragment = new productSpecificationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    RecyclerView productSpecificationRecyclerview;
    public List<productSpecificationModel> productSpecificationModelListF;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_product_specification, container, false);
        productSpecificationRecyclerview = view.findViewById(R.id.specification_recyclerView_fragmet_layout); 


        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(linearLayoutManager.VERTICAL);

        productSpecificationRecyclerview.setLayoutManager(linearLayoutManager);

       // List<productSpecificationModel> productSpecificationModelList = new ArrayList<>();
//        productSpecificationModelList.add(new productSpecificationModel(2,"packaj","1. Avatar(India) Pvt Ltd Ferns icon 2,Doddenakundi Village gangan ",2));
//        productSpecificationModelList.add(new productSpecificationModel(2,"BOX","1. Avatar(India) Pvt Ltd Ferns icon 2,Doddenakundi Village gangan ",2));
//        productSpecificationModelList.add(new productSpecificationModel(0,"General"));
//        productSpecificationModelList.add(new productSpecificationModel(1,"Ram","4"));
//        productSpecificationModelList.add(new productSpecificationModel(1,"Ram","4"));
//        productSpecificationModelList.add(new productSpecificationModel(1,"Ram","4"));
//        productSpecificationModelList.add(new productSpecificationModel(1,"Ram","4"));
//        productSpecificationModelList.add(new productSpecificationModel(1,"Ram","4"));
//        productSpecificationModelList.add(new productSpecificationModel(0,"Display"));
//        productSpecificationModelList.add(new productSpecificationModel(1,"Ram","4"));
//        productSpecificationModelList.add(new productSpecificationModel(1,"Ram","4"));
//        productSpecificationModelList.add(new productSpecificationModel(1,"Ram","4"));
//        productSpecificationModelList.add(new productSpecificationModel(1,"Ram","4"));
//        productSpecificationModelList.add(new productSpecificationModel(1,"Ram","4"));



        productSpecificationAdapter productSpecificationAdapter = new productSpecificationAdapter(productSpecificationModelListF);
        productSpecificationRecyclerview.setAdapter(productSpecificationAdapter);
        productSpecificationAdapter.notifyDataSetChanged();

        return view;

    }
}