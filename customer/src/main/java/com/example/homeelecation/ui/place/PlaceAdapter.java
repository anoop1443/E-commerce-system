package com.example.homeelecation.ui.place;

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

import com.example.homeelecation.R;

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

        holder.setPlace(image,title,body,catPrise,off,date,charge);


    }

    @Override
    public int getItemCount() {
        return placeModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        private final TextView title;
        private final TextView titleBody;
        private final TextView catPrise;
        private final TextView prise;
        private final TextView percentOff;
        private final TextView deliveryDate;
        private final TextView charge;
        private final TextView quantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.place_product_image);
            title = itemView.findViewById(R.id.place_product_title);
            titleBody = itemView.findViewById(R.id.place_title_body);
            catPrise = itemView.findViewById(R.id.place_cat_prise);
            prise = itemView.findViewById(R.id.place_Prise);
            percentOff = itemView.findViewById(R.id.place_off);
            deliveryDate = itemView.findViewById(R.id.place_work_day);
            charge = itemView.findViewById(R.id.place_service_amount);
            quantity = itemView.findViewById(R.id.place_product_qty);
        }

        private void setPlace(int imageRes, String titleText, String bodyText, int catPriceVal, int offPercent, String delivery, String serviceCharge) {
            imageView.setImageResource(imageRes);
            title.setText(titleText);
            titleBody.setText(bodyText);
            catPrise.setText("₹" + catPriceVal);
            percentOff.setText(offPercent + "% off");
            
            int discount = (catPriceVal * offPercent) / 100;
            int finalPrice = catPriceVal - discount;
            prise.setText("₹" + finalPrice);

            deliveryDate.setText(delivery);
            charge.setText(serviceCharge);

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
                            } else {
                                quantity.setText("Qty: " + quantityNo.getText());
                            }
                            quantityDialog.dismiss();
                        }
                    });

                    quantityDialog.show();
                }
            });
        }
    }
}
