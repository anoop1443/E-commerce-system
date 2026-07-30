package com.example.homeadmin.ui.Cart;

import static com.example.homeadmin.ui.Cart.CartFragment.loadingDialog;
import static com.example.homeadmin.ui.details.ProductDetailsActivity.running_cart_query;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.homeadmin.R;
import com.example.homeadmin.ui.DbLoadData;
import com.example.homeadmin.ui.details.ProductDetailsActivity;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

     static List<CartItemModel> cartItemModelList;
    @SuppressLint("StaticFieldLeak")
    private  static  TextView currentAmount;
    //private static int totalAmount = 0;

    private static boolean RemoveBtn;

    public CartAdapter(List<CartItemModel> cartItemModelList, TextView currentAmount, boolean removeBtn) {
        this.cartItemModelList = cartItemModelList;
        CartAdapter.currentAmount = currentAmount;
        RemoveBtn = removeBtn;
    }

    @Override
    public int getItemViewType(int position) {
        switch (cartItemModelList.get(position).getType()) {
            case 0:
                return CartItemModel.CART_ITEM_LAYOUT;
            case 1:
                return CartItemModel.CART_TOTAL_AMOUNT_LAYOUT;

            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case CartItemModel.CART_ITEM_LAYOUT:
                View cartItemLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout, parent, false);
                return new Cart(cartItemLayout);
            case CartItemModel.CART_TOTAL_AMOUNT_LAYOUT:
                View amountLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_total_amount_layout, parent, false);
                return new Amount(amountLayout);
            default:
                return null;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        switch (cartItemModelList.get(position).getType()) {
            case CartItemModel.CART_ITEM_LAYOUT:
                String productID = cartItemModelList.get(position).getProductID();
                String Photo = cartItemModelList.get(position).getProductImage();
                String productTitle = cartItemModelList.get(position).getProduct_Title();
                String productPrice = cartItemModelList.get(position).getProduct_Price();
                String productCutPrice = cartItemModelList.get(position).getProduct_cut_Price();
                String productCoupon = cartItemModelList.get(position).getProduct_Coupon();
                String work = cartItemModelList.get(position).getProduct_workDay();
                String chargeAmount = cartItemModelList.get(position).getProduct_Service_Amount();
                long Quantity = cartItemModelList.get(position).getProductQty();
                boolean inStock = cartItemModelList.get(position).isInStock();
                cartItemModelList.get(position).setDeliveryCharges(cartItemModelList.get(position).getDeliveryCharges());

                ((Cart) holder).setCart(productID, Photo, productTitle, productPrice, productCutPrice, productCoupon, work, chargeAmount, position, Quantity, inStock);
                break;

            case CartItemModel.CART_TOTAL_AMOUNT_LAYOUT:

                int totalItem = 0;
                int totalItemPrise = 0;
                int totalItemDiscount = 0;
                String deliveryCharges;
                int totalAmount = 0;


                for (int x = 0; x < cartItemModelList.size(); x++) {


                    try {
                        //int i = Integer.parseInt(input);
                       // totalItemPrise += Integer.parseInt(cartItemModelList.get(x).getProduct_cut_Price());
                       // totalAmount += Integer.parseInt(cartItemModelList.get(x).getProduct_Price());
                       // totalItemDiscount = totalItemPrise - totalAmount;
                        // totalAmount += totalItemPrise - totalItemDiscount;

                        if (cartItemModelList.get(position).getType() == CartItemModel.CART_TOTAL_AMOUNT_LAYOUT && cartItemModelList.get(x).isInStock()) {
                            int quantity = Integer.parseInt(String.valueOf(cartItemModelList.get(x).getProductQty()));
                            totalItem = totalItem+ quantity;
                             totalItemPrise = Integer.parseInt(cartItemModelList.get(x).getProduct_cut_Price())*quantity;
                            totalAmount = Integer.parseInt(cartItemModelList.get(x).getProduct_Price())*quantity;

                            totalItemDiscount = totalItemPrise-totalAmount;
                        }

                    } catch (NumberFormatException ex) { // handle your exception

                    }


                }

                if (totalAmount > 160) {
                    deliveryCharges = "FREE";
                } else {
                    deliveryCharges = "60";
                    totalAmount += 60;

                }

                cartItemModelList.get(position).setTotalItem(totalItem);
                cartItemModelList.get(position).setTotalItemPrise(totalItemPrise);
                cartItemModelList.get(position).setTotalItemDiscount(totalItemDiscount);
                cartItemModelList.get(position).setDeliveryCharges(deliveryCharges);
                cartItemModelList.get(position).setTotalAmount(totalAmount);

                ((Amount) holder).setAmount(totalItem, totalItemPrise, totalItemDiscount, deliveryCharges, totalAmount,position);

                return;

            default:
        }

    }


    @Override
    public int getItemCount() {
        return cartItemModelList.size();
    }

    public class Cart extends RecyclerView.ViewHolder {
        private final TextView productTitle, productPrice, productCutPrice, productCoupon, productQty, workDay, chargeAmount, remove;
        private final ImageView productImage;
        private final View divider;
        private final ConstraintLayout constraintLayout;


        public Cart(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.cart_product_image);
            productTitle = itemView.findViewById(R.id.cart_product_title);
            productPrice = itemView.findViewById(R.id.cart_product_prise);
            productCutPrice = itemView.findViewById(R.id.cart_product_cut_prise);
            productCoupon = itemView.findViewById(R.id.cart_coupon_offers);
            productQty = itemView.findViewById(R.id.cart_product_qty);
            workDay = itemView.findViewById(R.id.place_work_day);
            chargeAmount = itemView.findViewById(R.id.place_service_amount);
            remove = itemView.findViewById(R.id.cart_item_remove);
            divider = itemView.findViewById(R.id.cart_item_divider4);
            constraintLayout = itemView.findViewById(R.id.cart_itme_constraintLayout);
        }

        @SuppressLint("SetTextI18n")
        private void setCart(String productID, String photo, String Title, String Price, String CutPrice, String coupon, String DateDay, String serviceAmount, final int index, long quantity, boolean inStock) {

            //productImage.setImageResource(photo);
            // Glide.with(itemView.getContext()).load(photo).placeholder(R.drawable.ic_cart);
            Glide.with(itemView.getContext()).load(photo).apply(new RequestOptions().placeholder(R.drawable.ic_home)).into(productImage);

            if (RemoveBtn) {
                remove.setVisibility(View.VISIBLE);
            } else {
                remove.setVisibility(View.GONE);

            }

            productImage.setOnClickListener(v -> {
                Intent productDetailIntent = new Intent(itemView.getContext(), ProductDetailsActivity.class);
                productDetailIntent.putExtra("PRODUCT_ID", productID);
                itemView.getContext().startActivity(productDetailIntent);
            });

            remove.setOnClickListener(v -> {
                if (!ProductDetailsActivity.running_cart_query) {
                    running_cart_query = true;
                    //DbLoadData.removeFromCartList(index, itemView.getContext(), loadingDialog, currentAmount);
                    // Toast.makeText(itemView.getContext(), "CartList removed ", Toast.LENGTH_SHORT).show();
                }
            });


            if (inStock) {
                productTitle.setText(Title);
                productPrice.setText("Rs" + Price + "/-");
                productCutPrice.setText("Rs" + CutPrice + "/-");
                productCoupon.setText("free " + coupon + " coupon");
                workDay.setText(DateDay);
                chargeAmount.setText(serviceAmount);

                productQty.setText("Qty:"+quantity);
                productQty.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Dialog quantityDialog = new Dialog(itemView.getContext());
                        quantityDialog.setContentView(R.layout.quantity_dialog);
                        quantityDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

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
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onClick(View v) {

                                notifyItemChanged(cartItemModelList.size()-1);
                                    if (!TextUtils.isEmpty(quantityNo.getText().toString() )&& Long.parseLong( quantityNo.getText().toString())!=0) {

                                        productQty.setText("Qty: " + quantityNo.getText().toString());
                                        //DbLoadData.cartItemModelList.get(index).setProductQty(Long.valueOf(quantityNo.getText().toString()));
                                        cartItemModelList.get(index).setProductQty(Long.valueOf(quantityNo.getText().toString()));
                                    }else {
                                        Toast.makeText(itemView.getContext(), "Enter Max Quantity 1-9", Toast.LENGTH_SHORT).show();
                                    }
                                    quantityDialog.dismiss();

                               // }


                            }
                        });

                        quantityDialog.show();
                    }
                });
            } else {

                productTitle.setText(Title);
                // productImage.setColorFilter(Color.parseColor("#805F5E5E"));
                productPrice.setText("out of stock");
                productPrice.setAllCaps(true);
                productPrice.setTextSize(20);
                productPrice.setTextColor(Color.parseColor("#FFF62C08"));
                productCutPrice.setVisibility(View.INVISIBLE);
                productCoupon.setVisibility(View.INVISIBLE);
                workDay.setVisibility(View.GONE);
                chargeAmount.setVisibility(View.GONE);

                productQty.setVisibility(View.GONE);
                divider.setVisibility(View.GONE);


            }


        }
    }

    public static class Amount extends RecyclerView.ViewHolder {
        private final TextView totalItems;
        private final TextView totalItemPrice;
        private final TextView discountT;
        private final TextView serviceCharge;
        private final TextView totalAmount;
        private final TextView saveAmount;

        public Amount(@NonNull View itemView) {
            super(itemView);
            totalItems = itemView.findViewById(R.id.total_items);
            totalItemPrice = itemView.findViewById(R.id.total_items_price);
            discountT = itemView.findViewById(R.id.discount_price);
            serviceCharge = itemView.findViewById(R.id.service_amount);
            totalAmount = itemView.findViewById(R.id.totalAmnount);
            saveAmount = itemView.findViewById(R.id.save_amount);


        }

        @SuppressLint("SetTextI18n")
        private void setAmount(int totalItem, int totalItemPrise, int discount, String delivery, int totalItemAmount, int position) {
            totalItems.setText("prise(" + totalItem + ")item");
            totalItemPrice.setText("Rs " + totalItemPrise);
            discountT.setText("-Rs " + discount);
            if (delivery.equals("Free")) {
                serviceCharge.setText(delivery);
            } else {
                serviceCharge.setText("+Rs." + delivery + "/-");

            }


            totalAmount.setText("Rs." + totalItemAmount);
            currentAmount.setText("Rs." + totalItemAmount);
            saveAmount.setText("You will save Rs." + discount + "-/on this order");

            LinearLayout parent = (LinearLayout) currentAmount.getParent();
//            if (DbLoadData.cartItemModelList.size() == 0) {
//                DbLoadData.cartItemModelList.remove(DbLoadData.cartItemModelList.size() - 1);
//                parent.setVisibility(View.GONE);
//            } else {
//                parent.setVisibility(View.VISIBLE);
//            }

            if (cartItemModelList.size()==0){
                cartItemModelList.remove(cartItemModelList.size()-1);
                parent.setVisibility(View.GONE);
            }else {
                parent.setVisibility(View.VISIBLE);
            }
        }
    }
}
