package com.example.homeelecation.ui.coupons;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeelecation.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CouponFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CouponFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CouponFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CouponFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CouponFragment newInstance(String param1, String param2) {
        CouponFragment fragment = new CouponFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_coupon, container, false);

        recyclerView = view.findViewById(R.id.Coupon_recyclerview);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(layoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);


        List<CouponModel> couponModelList = new ArrayList<>();
        couponModelList.add(new CouponModel(" 1 Discount coupon","Till 13th March 2023","Get 20% OFF on any product above Rs. 500/- and below Rs.2500/-"));
        couponModelList.add(new CouponModel("Discount coupon","Till 13th March 2023","Get 20% OFF on any product above Rs. 500/- and below Rs.2500/-"));
        couponModelList.add(new CouponModel("Reward","Till 11th March 2023","Get 20% OFF on any product above Rs. 500/- and below Rs.2500/-"));
        couponModelList.add(new CouponModel("Buy now Get 1","Till 13th March 2023","Get 20% OFF on any product above Rs. 500/- and below Rs.2500/-"));
        couponModelList.add(new CouponModel("2 Buy 1 Coupon","Till 16th March 2023","Get 20% OFF on any product above Rs. 500/- and below Rs.2500/-"));
        couponModelList.add(new CouponModel("5 Item buy 1 MovieTicked","Till 10th March 2023","Get 20% OFF on any product above Rs. 500/- and below Rs.2500/-"));
        couponModelList.add(new CouponModel("3 Item buy 5 Reward","Till 1th March 2023","Get 20% OFF on any product above Rs. 500/- and below Rs.2500/-"));
        couponModelList.add(new CouponModel("Axis bank ","Till 3th March 2023","Get 20% OFF on any product above Rs. 500/- and below Rs.2500/-"));
        couponModelList.add(new CouponModel("Axis direct ","Till 23th April 2023","Get 20% OFF on any product above Rs. 500/- and below Rs.2500/-"));
        couponModelList.add(new CouponModel("6 Coupon your","Till 19th May 2023","Get 20% OFF on any product above Rs. 500/- and below Rs.2500/-"));
        couponModelList.add(new CouponModel("Reward","Till 14th Jun 2023","Get 20% OFF on any product above Rs. 500/- and below Rs.2500/-"));
        couponModelList.add(new CouponModel("Discount coupon","Till 13th Feb 2023","Get 20% OFF on any product above Rs. 500/- and below Rs.2500/-"));
        couponModelList.add(new CouponModel("Google play store ","Till 11th March 2023","Get 20% OFF on any product above Rs. 500/- and below Rs.2500/-"));
        couponModelList.add(new CouponModel("Airtel Reward","Till 10th March 2023","Get 20% OFF on any product above Rs. 500/- and below Rs.2500/-"));
        couponModelList.add(new CouponModel("Flipkart Reward","Till 1th Aug 2023","Get 20% OFF on any product above Rs. 500/- and below Rs.2500/-"));
        couponModelList.add(new CouponModel("Youtube coupon","Till 16th Dec 2023","Get 20% OFF on any product above Rs. 500/- and below Rs.2500/-"));
        couponModelList.add(new CouponModel("Reward","Till 9th March 2023","Get 20% OFF on any product above Rs. 500/- and below Rs.2500/-"));
        couponModelList.add(new CouponModel("5 Discount coupon","Till 3th March 2023","Get 20% OFF on any product above Rs. 500/- and below Rs.2500/-"));
        couponModelList.add(new CouponModel("2 Coupon your","Till 6th March 2023","Get 20% OFF on any product above Rs. 500/- and below Rs.2500/-"));

        CouponAdapter adapter = new CouponAdapter(couponModelList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        return view;
    }
}