package com.example.homeelecation.ui.Cart;

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
import com.example.homeelecation.R;
import com.example.homeelecation.ui.details.ProductDetailsActivity;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<CartItemModel> cartItemModelList;
    @SuppressLint("StaticFieldLeak")
    private final TextView currentAmount;
    private final boolean RemoveBtn;
    private final CartViewModel cartViewModel;

    public CartAdapter(List<CartItemModel> cartItemModelList, TextView currentAmount, boolean removeBtn, CartViewModel cartViewModel) {
        this.cartItemModelList = cartItemModelList;
        this.currentAmount = currentAmount;
        this.RemoveBtn = removeBtn;
        this.cartViewModel = cartViewModel;
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
                int layoutRes = RemoveBtn ? R.layout.cart_item_layout : R.layout.place_item_layout;
                View cartItemLayout = LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
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
                String productTitle = cartItemModelList.get(position).getProductTitle();
                String productPrice = cartItemModelList.get(position).getProductPrice();
                String productCutPrice = cartItemModelList.get(position).getProductCutPrice();
                String productCoupon = cartItemModelList.get(position).getProductCoupon();
                String work = cartItemModelList.get(position).getProductWorkDay();
                String chargeAmount = cartItemModelList.get(position).getProductServiceAmount();
                long Quantity = cartItemModelList.get(position).getProductQty();
                boolean inStock = cartItemModelList.get(position).isInStock();
                
                // Unifying productServiceAmount with deliveryCharges as requested
                cartItemModelList.get(position).setDeliveryCharges(chargeAmount);

                ((Cart) holder).setCart(productID, Photo, productTitle, productPrice, productCutPrice, productCoupon, work, chargeAmount, position, Quantity, inStock);
                break;

            case CartItemModel.CART_TOTAL_AMOUNT_LAYOUT:

                int totalItem = 0;
                int totalItemPrise = 0;
                int totalItemDiscount = 0;
                String deliveryCharges = "0";
                int totalAmount = 0;
                long totalServiceCharge = 0;


                for (int x = 0; x < cartItemModelList.size(); x++) {
                    CartItemModel model = cartItemModelList.get(x);

                    if (model.getType() == CartItemModel.CART_ITEM_LAYOUT && model.isInStock()) {
                        try {
                            int quantity = Math.toIntExact(model.getProductQty());
                            totalItem += quantity;
                            totalItemPrise += Integer.parseInt(model.getProductCutPrice()) * quantity;
                            totalAmount += Integer.parseInt(model.getProductPrice()) * quantity;

                            // सर्विस चार्ज कैलकुलेशन
                            String sCharge = model.getProductServiceAmount();
                            if (sCharge != null && !sCharge.isEmpty() && !sCharge.equalsIgnoreCase("free")) {
                                totalServiceCharge += Long.parseLong(sCharge) * quantity;
                            }
                        } catch (Exception e) {
                            // Parsing error handle करें
                        }
                    }
                }
                totalItemDiscount = totalItemPrise-totalAmount;

                deliveryCharges = String.valueOf(totalServiceCharge);
                totalAmount +=  totalServiceCharge;


                cartItemModelList.get(position).setTotalItem(totalItem);
                cartItemModelList.get(position).setTotalItemPrise(totalItemPrise);
                cartItemModelList.get(position).setTotalItemDiscount(totalItemDiscount);
                cartItemModelList.get(position).setDeliveryCharges(deliveryCharges);
                cartItemModelList.get(position).setTotalAmount(totalAmount);

                ((Amount) holder).setAmount(totalItem, totalItemPrise, totalItemDiscount, deliveryCharges, totalAmount, totalServiceCharge);

                return;

            default:
        }

    }


    @Override
    public int getItemCount() {
        return cartItemModelList.size();
    }

    public void updateList(List<CartItemModel> cartItems) {
        cartItemModelList.clear();
        cartItemModelList.addAll(cartItems);
        notifyDataSetChanged();
    }

    public class Cart extends RecyclerView.ViewHolder {
        private final TextView productTitle, productPrice, productCutPrice, productCoupon, productQty, workDay, chargeAmount, remove;
        private final ImageView productImage;
        private final View divider;
        private final ConstraintLayout constraintLayout;


        public Cart(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.cart_product_image) != null ? itemView.findViewById(R.id.cart_product_image) : itemView.findViewById(R.id.place_product_image);
            productTitle = itemView.findViewById(R.id.cart_product_title) != null ? itemView.findViewById(R.id.cart_product_title) : itemView.findViewById(R.id.place_product_title);
            productPrice = itemView.findViewById(R.id.cart_product_prise) != null ? itemView.findViewById(R.id.cart_product_prise) : itemView.findViewById(R.id.place_Prise);
            productCutPrice = itemView.findViewById(R.id.cart_product_cut_prise) != null ? itemView.findViewById(R.id.cart_product_cut_prise) : itemView.findViewById(R.id.place_cat_prise);
            productCoupon = itemView.findViewById(R.id.cart_coupon_offers) != null ? itemView.findViewById(R.id.cart_coupon_offers) : itemView.findViewById(R.id.place_off);
            productQty = itemView.findViewById(R.id.cart_product_qty) != null ? itemView.findViewById(R.id.cart_product_qty) : itemView.findViewById(R.id.place_product_qty);
            workDay = itemView.findViewById(R.id.place_work_day);
            chargeAmount = itemView.findViewById(R.id.place_service_amount);
            remove = itemView.findViewById(R.id.cart_item_remove);
            divider = itemView.findViewById(R.id.cart_item_divider4) != null ? itemView.findViewById(R.id.cart_item_divider4) : itemView.findViewById(R.id.divider_place);
            constraintLayout = itemView.findViewById(R.id.cart_itme_constraintLayout);
        }

        @SuppressLint("SetTextI18n")
        private void setCart(String productID, String photo, String Title, String Price, String CutPrice, String coupon, String DateDay, String serviceAmount, final int index, long quantity, boolean inStock) {

            //productImage.setImageResource(photo);
            // Glide.with(itemView.getContext()).load(photo).placeholder(R.drawable.ic_cart);
            Glide.with(itemView.getContext()).load(photo).apply(new RequestOptions().placeholder(R.drawable.ic_home)).into(productImage);

            if (RemoveBtn) {
                if (remove != null) remove.setVisibility(View.VISIBLE);
            } else {
                if (remove != null) remove.setVisibility(View.GONE);
            }

            productImage.setOnClickListener(v -> {
                Intent productDetailIntent = new Intent(itemView.getContext(), ProductDetailsActivity.class);
                productDetailIntent.putExtra("PRODUCT_ID", productID);
                itemView.getContext().startActivity(productDetailIntent);
            });

            if (remove != null) {
                remove.setOnClickListener(v -> {
                    cartViewModel.removeFromCart(productID);
                });
            }


            if (inStock) {
                productTitle.setText(Title);
                productPrice.setText("Rs" + Price + "/-");
                productCutPrice.setText("Rs" + CutPrice + "/-");
                productCoupon.setText("free " + coupon + " coupon");
                workDay.setText(DateDay);

                if (serviceAmount == null || serviceAmount.equals("0") || serviceAmount.equalsIgnoreCase("free")) {
                    chargeAmount.setText("Free Service");
                } else {
                    chargeAmount.setText("Service: Rs." + serviceAmount);
                }

                productQty.setText("Qty:"+quantity);
                productQty.setOnClickListener(v -> {
                    Dialog quantityDialog = new Dialog(itemView.getContext());
                    quantityDialog.setContentView(R.layout.quantity_dialog);
                    quantityDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                    quantityDialog.setCancelable(false);
                    EditText quantityNo = quantityDialog.findViewById(R.id.quantity_no);
                    Button cancel = quantityDialog.findViewById(R.id.quantity_cancel_btn);
                    Button ok = quantityDialog.findViewById(R.id.quantity_ok_btn);

                    cancel.setOnClickListener(v1 -> quantityDialog.dismiss());

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
                });
            } else {

                productTitle.setText(Title);
                // productImage.setColorFilter(Color.parseColor("#805F5E5E"));
                productPrice.setText("out of stock");
                productPrice.setAllCaps(true);
                productPrice.setTextSize(20);
                productPrice.setTextColor(Color.parseColor("#FFF62C08"));
                if (productCutPrice != null) productCutPrice.setVisibility(View.INVISIBLE);
                if (productCoupon != null) productCoupon.setVisibility(View.INVISIBLE);
                if (workDay != null) workDay.setVisibility(View.GONE);
                if (chargeAmount != null) chargeAmount.setVisibility(View.GONE);

                if (productQty != null) productQty.setVisibility(View.GONE);
                if (divider != null) divider.setVisibility(View.GONE);


            }


        }
    }

    public class Amount extends RecyclerView.ViewHolder {
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
        private void setAmount(int totalItem, int totalItemPrise, int discount, String delivery, int totalItemAmount, long servicePrice) {
            totalItems.setText("prise(" + totalItem + ")item");
            totalItemPrice.setText("Rs " + totalItemPrise);
            discountT.setText("-Rs " + discount);
            if (servicePrice > 0) {
                serviceCharge.setText("Rs." + servicePrice + "/-");
            } else {
                serviceCharge.setText("FREE");
            }


            totalAmount.setText("Rs." + totalItemAmount);
            if (currentAmount != null) {
                currentAmount.setText("Rs." + totalItemAmount);
                LinearLayout parent = (LinearLayout) currentAmount.getParent();

                if (cartItemModelList.isEmpty()){
                    parent.setVisibility(View.GONE);
                }else {
                    parent.setVisibility(View.VISIBLE);
                }
            }
            saveAmount.setText("You will save Rs." + discount + "-/on this order");
        }
    }


}
