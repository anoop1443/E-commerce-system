package com.example.homeadmin.ui.gridView;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.homeadmin.R;
import com.example.homeadmin.ui.details.ProductDetailsActivity;
import com.example.homeadmin.ui.horizontal.HorizontalProductScrollModel;

import java.util.List;

public class GridAllViewAdapter extends BaseAdapter {

    List<HorizontalProductScrollModel> gripAllviewlist;

    public GridAllViewAdapter(List<HorizontalProductScrollModel> gripAllviewlist) {
        this.gripAllviewlist = gripAllviewlist;
    }

    @Override
    public int getCount() {
        return gripAllviewlist.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view ;


        if (convertView == null){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_scroll_item_layout,null);
            view.setElevation(0);
            view.setBackgroundColor(Color.parseColor("#ffffff"));
            ImageView productImage = view.findViewById(R.id.h_s_product_image);
            TextView productTitle = view.findViewById(R.id.h_s_product_titel);
            TextView productDescription = view.findViewById(R.id.h_s_product_description);
            TextView productPrice = view.findViewById(R.id.h_s_product_price);



            //productImage.setImageResource(gripAllviewlist.get(position).getProductImage());
            Glide.with(view).load(gripAllviewlist.get(position).getProductImage()).into(productImage);
            productTitle.setText(gripAllviewlist.get(position).getProductTitle());
            productDescription.setText(gripAllviewlist.get(position).getProductDescription());
            productPrice.setText("Rs."+gripAllviewlist.get(position).getProductPrice()+"/-");

            if (!productTitle.equals("")) {


                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(parent.getContext(), ProductDetailsActivity.class);
                        intent.putExtra("PRODUCT_ID",gripAllviewlist.get(position).getProductId());
                        parent.getContext().startActivities(new Intent[]{intent});
                    }
                });
            }


        }else {
            view = convertView;

        }





        return view;
    }
}
