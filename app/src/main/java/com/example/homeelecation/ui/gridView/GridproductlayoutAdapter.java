package com.example.homeelecation.ui.gridView;


import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.homeelecation.R;
import com.example.homeelecation.ui.details.ProductDeteilsActivity;
import com.example.homeelecation.ui.horizontal.HorizontalProductScrollModel;

import java.util.List;

public class GridproductlayoutAdapter extends BaseAdapter {

    List<HorizontalProductScrollModel> horizontalproductscrollModelList;

    public GridproductlayoutAdapter(List<HorizontalProductScrollModel> horizontalproductscrollModelList) {
        this.horizontalproductscrollModelList = horizontalproductscrollModelList;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(  int position ) {

        return 0;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        View view;


        if (convertView == null){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_scroll_item_layout,null);
            view.setElevation(0);
            view.setBackgroundColor(Color.parseColor("#ffffff"));
            ImageView productImage = view.findViewById(R.id.h_s_product_image);
            TextView productTitle = view.findViewById(R.id.h_s_product_titel);
            TextView productDescription = view.findViewById(R.id.h_s_product_description);
            TextView productPrice = view.findViewById(R.id.h_s_product_price);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(parent.getContext(), ProductDeteilsActivity.class);
                    intent.putExtra("PRODUCT_ID",horizontalproductscrollModelList.get(position).getProductId());
                    parent.getContext().startActivity(intent);


                }
            });

           // productImage.setImageResource(horizontalproductscrollModelList.get(position).getProductImage());
            productTitle.setText(horizontalproductscrollModelList.get(position).getProductTitle());
            productDescription.setText(horizontalproductscrollModelList.get(position).getProductDescription());
            productPrice.setText(horizontalproductscrollModelList.get(position).getProductPrice());


        }else {
            view = convertView;

        }



        return view;
    }

}
