package com.example.homeelecation.ui.details;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeelecation.R;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link productMoreInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class productMoreInfoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public productMoreInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MoreInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static productMoreInfoFragment newInstance(String param1, String param2) {
        productMoreInfoFragment fragment = new productMoreInfoFragment();
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
    RecyclerView recyclerView;
    TextView Description1;

    public  List<productSpecificationModel> productSpecificationModelList1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_more_info, container, false);
        recyclerView = view.findViewById(R.id.more_recycler_view);


//
//        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getContext());
//        linearLayoutManager.setOrientation(linearLayoutManager.VERTICAL);
//
//        recyclerView.setLayoutManager(linearLayoutManager);
//
//        productSpecificationModelList1 = new ArrayList<>();
//        productSpecificationModelList1.add(new productSpecificationModel(0,"General"));
//        productSpecificationModelList1.add(new productSpecificationModel(2,"Manufacture Detail's","1. Avatar(India) Pvt Ltd Ferns icon 2,Doddenakundi Village gangan ",2));
//        productSpecificationModelList1.add(new productSpecificationModel(2,"Manufacture Detail's","1. Avatar(India) Pvt Ltd Ferns icon 2,Doddenakundi Village gangan ",2));
//        productSpecificationModelList1.add(new productSpecificationModel(2,"Manufacture Detail's","1. Avatar(India) Pvt Ltd Ferns icon 2,Doddenakundi Village gangan ",2));
//        productSpecificationModelList1.add(new productSpecificationModel(2,"Manufacture Detail's","1. Avatar(India) Pvt Ltd Ferns icon 2,Doddenakundi Village gangan ",2));
//        productSpecificationModelList1.add(new productSpecificationModel(2,"Manufacture Detail's","1. Avatar(India) Pvt Ltd Ferns icon 2,Doddenakundi Village gangan ",2));
//        productSpecificationModelList1.add(new productSpecificationModel(2,"Manufacture Detail's","1. Avatar(India) Pvt Ltd Ferns icon 2,Doddenakundi Village gangan ",2));
//        productSpecificationModelList1.add(new productSpecificationModel(2,"Manufacture Detail's","1. Avatar(India) Pvt Ltd Ferns icon 2,Doddenakundi Village gangan ",2));
//        productSpecificationModelList1.add(new productSpecificationModel(2,"Manufacture Detail's","1. Avatar(India) Pvt Ltd Ferns icon 2,Doddenakundi Village gangan ",2));
//        productSpecificationModelList1.add(new productSpecificationModel(2,"Manufacture Detail's","1. Avatar(India) Pvt Ltd Ferns icon 2,Doddenakundi Village gangan ",2));
//        productSpecificationModelList1.add(new productSpecificationModel(2,"Manufacture Detail's","1. Avatar(India) Pvt Ltd Ferns icon 2,Doddenakundi Village gangan ",2));
//        productSpecificationModelList1.add(new productSpecificationModel(2,"Manufacture Detail's","1. Avatar(India) Pvt Ltd Ferns icon 2,Doddenakundi Village gangan ",2));
//        productSpecificationModelList1.add(new productSpecificationModel(2,"Manufacture Detail's","1. Avatar(India) Pvt Ltd Ferns icon 2,Doddenakundi Village gangan ",2));
//        productSpecificationModelList1.add(new productSpecificationModel(2,"Manufacture Detail's","1. Avatar(India) Pvt Ltd Ferns icon 2,Doddenakundi Village gangan ",2));
//        productSpecificationModelList1.add(new productSpecificationModel(2,"Manufacture Detail's","1. Avatar(India) Pvt Ltd Ferns icon 2,Doddenakundi Village gangan ",2));
//        productSpecificationModelList1.add(new productSpecificationModel(2,"Manufacture Detail's","1. Avatar(India) Pvt Ltd Ferns icon 2,Doddenakundi Village gangan ",2));
//        productSpecificationModelList1.add(new productSpecificationModel(2,"Manufacture Detail's","1. Avatar(India) Pvt Ltd Ferns icon 2,Doddenakundi Village gangan ",2));
//
//
//
//        productSpecificationAdapter productmoreAdapter = new productSpecificationAdapter(productSpecificationModelList1);
//        recyclerView.setAdapter(productmoreAdapter);
//        productmoreAdapter.notifyDataSetChanged();

        return view;
    }
}