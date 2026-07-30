package com.example.homeadmin.ui.place;

import android.app.Dialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeadmin.R;

import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {


    private List<PlaceModel> placeModelList;

    public PlaceAdapter(List<PlaceModel> placeModelList) {
        this.placeModelList = placeModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_item_layout,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        int image = placeModelList.get(position).getImage();
        String title = placeModelList.get(position).getTitle();
        String body = placeModelList.get(position).getTitleBody();
        int rating = placeModelList.get(position).getTotalRating();
        int catPrise = placeModelList.get(position).getCatPrise();
        int off = placeModelList.get(position).getPercentOff();
        String applied = placeModelList.get(position).getOffersApplied();
        String available = placeModelList.get(position).getOffersAvailable();
        String date = placeModelList.get(position).getDeliveryDate();
        String charge = placeModelList.get(position).getCharges();

        holder.setPlace(image,title,body,rating,catPrise,off,applied,available,date,charge);


    }

    @Override
    public int getItemCount() {
        return placeModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

         ImageView imageView;

       private final TextView title;
        private final TextView titleBody;
        private final TextView totalRating;
        private final TextView catPrise;
        private final TextView prise;
        private final TextView percentOff;
        private final TextView applied;
        private final TextView available;
        private final TextView deliveryDate;
        private final TextView charge;
        private final TextView quantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.place_product_image);
            title = itemView.findViewById(R.id.place_product_title);
            titleBody = itemView.findViewById(R.id.place_title_body);
            totalRating = itemView.findViewById(R.id.place_total_rating);
            catPrise = itemView.findViewById(R.id.place_cat_prise);
            prise = itemView.findViewById(R.id.place_Prise);
            percentOff = itemView.findViewById(R.id.place_off);
            applied = itemView.findViewById(R.id.place_offers_applied);
            available = itemView.findViewById(R.id.place_offers_available);
            deliveryDate = itemView.findViewById(R.id.place_work_day);
            charge = itemView.findViewById(R.id.place_service_amount);
            quantity = itemView.findViewById(R.id.place_product_qty);



        }
        private void setPlace(int Image,String Title,String body,int TotalRating,int CatPrise,int PercentOff,String Applied,String Available,String Delivery,String Charge ){
             imageView.setImageResource(Image);
            title.setText(Title);
            titleBody.setText(body);
            totalRating.setText("("+TotalRating+"k)");
            catPrise.setText("₹"+CatPrise);
            percentOff.setText(PercentOff+"%of");
            int fix = CatPrise*PercentOff/100;
            int car = CatPrise-fix;
            prise.setText("₹"+car);

            applied.setText(Applied);
            available.setText("."+Available);
            deliveryDate.setText(Delivery);
            charge.setText(Charge);

            quantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog quantityDialog = new Dialog(itemView.getContext());
                    quantityDialog.setContentView(R.layout.quantity_dialog);
                    quantityDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);

                    quantityDialog.setCancelable(false);
                    EditText quantityNo = quantityDialog.findViewById(R.id.quantity_no);
                    Button cancel = quantityDialog.findViewById(R.id.quantity_cancel_btn);
                    Button ok = quantityDialog.findViewById(R.id.quantity_ok_btn);

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            quantityDialog.dismiss();
                        }
                    });

                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (TextUtils.isEmpty(quantityNo.getText().toString())){
                                quantity.setText("Qty: 1");
                                quantityDialog.dismiss();
                            }else {
                                quantity.setText("Qty: "+quantityNo.getText());
                                quantityDialog.dismiss();

                            }

                        }
                    });

                    quantityDialog.show();
                }
            });



        }


    }

}
