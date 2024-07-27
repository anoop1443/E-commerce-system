package com.example.homeelecation.ui.orders;

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

public class My_OrderFragment extends Fragment {

    RecyclerView myOrderRecyclerView;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_my__order,container , false);


        myOrderRecyclerView = view.findViewById(R.id.my_order_recyclerView);



        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(layoutManager.VERTICAL);
        myOrderRecyclerView.setLayoutManager(layoutManager);






        List<MyOrderItemModel> myOrderItemModelList = new ArrayList<>();
        myOrderItemModelList.add(new MyOrderItemModel(R.drawable.tebal_fan,2,"Table fan","lDelivered Mon 15th,jan 2019"));
        myOrderItemModelList.add(new MyOrderItemModel(R.drawable.tebal_fan,1,"Table fan"," Delivered Mon 15th,jan 2019cancelled "));
        myOrderItemModelList.add(new MyOrderItemModel(R.drawable.horizontla_fan,3,"Table fan","Cancelled"));
        myOrderItemModelList.add(new MyOrderItemModel(R.drawable.haeter,4,"Table fan","Delivered Mon 15th,jan 2019"));
        myOrderItemModelList.add(new MyOrderItemModel(R.drawable.ic__reward,3,"Table fan","Cancelled"));
        myOrderItemModelList.add(new MyOrderItemModel(R.drawable.ic_cart,3,"Table fan","Cancelled"));
        myOrderItemModelList.add(new MyOrderItemModel(R.drawable.tebal_fan,3,"Table fan","Cancelled"));
        myOrderItemModelList.add(new MyOrderItemModel(R.drawable.img,3,"Table fan","Cancelled"));
        myOrderItemModelList.add(new MyOrderItemModel(R.drawable.tebal_fan,3,"Table fan","Cancelled"));
        myOrderItemModelList.add(new MyOrderItemModel(R.drawable.haeter,3,"Table fan","Cancelled"));
        myOrderItemModelList.add(new MyOrderItemModel(R.drawable.img,3,"Table fan","Cancelled"));
        myOrderItemModelList.add(new MyOrderItemModel(R.drawable.tebal_fan,3,"Table fan","Cancelled"));
        myOrderItemModelList.add(new MyOrderItemModel(R.drawable.strip_ads,3,"Table fan","Cancelled"));
        myOrderItemModelList.add(new MyOrderItemModel(R.drawable.tebal_fan,3,"Table fan","  jan 14th Cancelled"));
        myOrderItemModelList.add(new MyOrderItemModel(R.drawable.ic_home,3,"Table fan","Cancelled"));
        myOrderItemModelList.add(new MyOrderItemModel(R.drawable.img,2,"cabal 6.00mm","Fer 17th 2018"));
        myOrderItemModelList.add(new MyOrderItemModel(R.drawable.haeter,2,"cabal 6.00mm","Mar 17th 2018"));
        myOrderItemModelList.add(new MyOrderItemModel(R.drawable.ic_dot,2,"cabal 6.00mm","Cancelled"));



        MyOrderItemAdapter adapter = new MyOrderItemAdapter(myOrderItemModelList);
        myOrderRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();





        return view;
    }
}