package com.example.homeelecation.ui.orders;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.homeelecation.R;
import com.example.homeelecation.ui.DbLoadData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyOrderItemAdapter extends RecyclerView.Adapter<MyOrderItemAdapter.ViewHolder> {


    private final List<MyOrderItemModel> myOrderItemModelList;

    public MyOrderItemAdapter(List<MyOrderItemModel> myOrderItemModelList) {
        this.myOrderItemModelList = myOrderItemModelList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_orders_layout, parent, false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String title = myOrderItemModelList.get(position).getProductTitle();
        String image = myOrderItemModelList.get(position).getProductImage();
        String orderStatus = myOrderItemModelList.get(position).getOrderStatus();
        int rating = myOrderItemModelList.get(position).getRating();
        String productId = myOrderItemModelList.get(position).getProductID();
        Date date;

        switch (orderStatus) {

            case "Ordered":
            case "Payment Failed":
            case "Failed":
                date = myOrderItemModelList.get(position).getOrderedDate();
                break;

            case "Packed":
                date = myOrderItemModelList.get(position).getPackedDate();
                break;

            case "Shipped":
                date = myOrderItemModelList.get(position).getShippedDate();
                break;

            case "Delivered":
                date = myOrderItemModelList.get(position).getDeliveredDate();
                break;


            default:
                date = myOrderItemModelList.get(position).getCancelledDate();


        }

        holder.setMY(title, image, orderStatus, date, rating, position, productId);
        holder.itemView.setOnClickListener(v -> {
            //MyOrderItemModel currentModel = myOrderItemModelList.get(position);

            Intent intent = new Intent(holder.itemView.getContext(), Orders_DetailsActivity3.class);
            intent.putExtra("ORDER_ID", myOrderItemModelList.get(position).getOrderID());
            intent.putExtra("PRODUCT_ID", myOrderItemModelList.get(position).getProductID());
            intent.putExtra("POSITION", position);
            holder.itemView.getContext().startActivities(new Intent[]{intent});


        });


    }

    @Override
    public int getItemCount() {
        return myOrderItemModelList.size();
    }

    public void updateList(List<MyOrderItemModel> orders) {
        myOrderItemModelList.clear();
        myOrderItemModelList.addAll(orders);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageResource;
        public final ImageView Indicator;
        private final TextView title;
        private final TextView deliveryStu;
        private final LinearLayout rateNowContainer;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageResource = itemView.findViewById(R.id.product_image);
            Indicator = itemView.findViewById(R.id.orDerIndicator);
            title = itemView.findViewById(R.id.product_title);
            deliveryStu = itemView.findViewById(R.id.order_delivered_date);
            rateNowContainer = itemView.findViewById(R.id.rate_now_contenr);
        }

        private void setMY(String productTitle, String resource, String orderStatus, Date date, int rating, int position, String productId) {
            //imageResource.setImageResource(resource);
            title.setText(productTitle.length() > 33 ? productTitle.substring(0, 33) + "..." : productTitle);

            Glide.with(itemView.getContext()).load(resource).into(imageResource);


            if (orderStatus.equals("Cancelled") || orderStatus.equals("Payment Failed") || orderStatus.equals("Failed")) {
                Indicator.setImageTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(R.color.red)));
                rateNowContainer.setVisibility(View.GONE);

            } else {
                Indicator.setImageTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(R.color.green)));

            }
//            simpleDateFormat = new SimpleDateFormat("EE, dd MMM yy, hh:mm aa");
//
//            String data = String.valueOf(simpleDateFormat.format(date));
//
//            deliveryStu.setText(orderStatus + " " + data);

            // --- Date Format ---
            if (date != null) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EE, dd MMM yy, hh:mm aa");
                String dataStr = String.valueOf(simpleDateFormat.format(date));
                deliveryStu.setText(orderStatus + " " + dataStr);
            } else {
                deliveryStu.setText(orderStatus);
            }

            if (orderStatus.equals("Payment Failed")) {
                deliveryStu.setTextColor(Color.RED);
            } else {
                deliveryStu.setTextColor(Color.GRAY); // Ya jo bhi aapka default color ho
            }


            //rating
            SetRating(rating);
            for (int x = 0; x < rateNowContainer.getChildCount(); x++) {
                final int starPosition = x;
                rateNowContainer.getChildAt(x).setOnClickListener(v -> {
                    SetRating(starPosition);

                    DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Product_Details").document(productId);
                    FirebaseFirestore.getInstance().runTransaction(new Transaction.Function<Object>() {
                        /**
                         * @param transaction
                         * @return
                         * @throws FirebaseFirestoreException
                         */
                        @Nullable
                        @Override
                        public Object apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

                            DocumentSnapshot documentSnapshot = transaction.get(documentReference);
                            long increase = documentSnapshot.getLong(starPosition + 1 + "_star") + 1;
                            if (rating != 0) {
                                long decrease = documentSnapshot.getLong(rating + 1 + "_star") - 1;
                                transaction.update(documentReference, starPosition + 1 + "_star", increase);
                                transaction.update(documentReference, rating + 1 + "_star", decrease);
                            } else {
                                transaction.update(documentReference, starPosition + 1 + "_star", increase);

                            }

                            return null;
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Object>() {
                        /**
                         * @param o
                         */
                        @Override
                        public void onSuccess(Object o) {

                            Map<String, Object> myRating = new HashMap<>();
                            if (DbLoadData.ratingsId.contains(productId)) {
                                myRating.put("rating_" + DbLoadData.ratingsId.indexOf(productId), (long) starPosition + 1);
                            } else {
                                myRating.put("list_size", (long) DbLoadData.ratingsId.size() + 1);
                                myRating.put("product_ID_" + DbLoadData.ratingsId.size(), productId);
                                myRating.put("rating_" + DbLoadData.ratingsId.size(), (long) starPosition + 1);
                            }

                            FirebaseFirestore.getInstance().collection("USER").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_RATINGS")
                                    .update(myRating).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {

                                                DbLoadData.myOrderItemModelList.get(position).setRating(starPosition);
                                                if (DbLoadData.ratingsId.contains(productId)) {
                                                    DbLoadData.myRatings.set(DbLoadData.ratingsId.indexOf(productId), (long) starPosition + 1);
                                                } else {
                                                    DbLoadData.ratingsId.add(productId);
                                                    DbLoadData.myRatings.add((long) starPosition + 1);
                                                }

                                            } else {
                                                String error = task.getException().getMessage();
                                                Toast.makeText(itemView.getContext(), error, Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

                });
            }
            //rating
        }

        /////rating
        private void SetRating(int starPosition) {
            for (int x = 0; x < rateNowContainer.getChildCount(); x++) {
                ImageView starBtn = (ImageView) rateNowContainer.getChildAt(x);
                starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#FFADB1AD")));
                if (x <= starPosition) {
                    starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#05A620")));
                }
            }
        }

        /////rating
    }
}
