package com.example.homeelecation.ui;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;
import static com.example.homeelecation.ui.details.ProductDetailsActivity.addToWishListButton;
import static com.example.homeelecation.ui.details.ProductDetailsActivity.productID;
import static com.example.homeelecation.ui.wishList.Wishlist_Fragment.wishlistAdapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeelecation.LoginActivity;
import com.example.homeelecation.R;
import com.example.homeelecation.ui.Cart.CartItemModel;
import com.example.homeelecation.ui.address.Add_delivery_address_Activity3;
import com.example.homeelecation.ui.address.AddressesSelectModel;
import com.example.homeelecation.ui.categoryView.CategoryModel;
import com.example.homeelecation.ui.details.ProductDetailsActivity;
import com.example.homeelecation.ui.home.HomepageAdapter;
import com.example.homeelecation.ui.home.HomepageModel;
import com.example.homeelecation.ui.horizontal.HorizontalProductScrollModel;
import com.example.homeelecation.ui.notification.NotificationActivity;
import com.example.homeelecation.ui.notification.NotificationModel;
import com.example.homeelecation.ui.orders.MyOrderItemAdapter;
import com.example.homeelecation.ui.orders.MyOrderItemModel;
import com.example.homeelecation.ui.place.PLaceActivity3;
import com.example.homeelecation.ui.slideshow.SliderModel;
import com.example.homeelecation.ui.wishList.WishlistModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class DbLoadData {

    public interface MyCompleteListener {
        void onComplete();
        void onFailure(String errorMessage);
    }
    @SuppressLint("StaticFieldLeak")
    public static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public static String fullName,mobile,email,profileImage,gender;

    public static List<CategoryModel> categoryModelList = new ArrayList<>();
    public static List<String> loadedCategoriesName = new ArrayList<>();
    public static List<HomepageModel> homepageModelList = new ArrayList<>();
    public static List<SliderModel> sliderModelList = new ArrayList<>();
    public static List<List<HomepageModel>> lists = new ArrayList<>();

    public static List<String> wishLisT = new ArrayList<>();
    public static List<WishlistModel> wishlistModelList = new ArrayList<>();
    public static List<String> ratingsId = new ArrayList<>();
    public static List<Long> myRatings = new ArrayList<>();
    public static List<String> cartLis = new ArrayList<>();
    public static List<CartItemModel> cartItemModelList = new ArrayList<>();

    public static int selectedAddresses = -1;
    public static List<AddressesSelectModel> addressesSelectModelList = new ArrayList<>();
    public static List<MyOrderItemModel> myOrderItemModelList = new ArrayList<>();
    public static List<NotificationModel> notificationModelList = new ArrayList<>();
    private static ListenerRegistration registration;

    public static void loadCategory(final  MyCompleteListener myCompleteListener) {
        cartItemModelList.clear();

        firebaseFirestore.collection("CATEGORY").orderBy("index")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            categoryModelList.add(new CategoryModel(
                                    document.getId(),
                                    Objects.requireNonNull(document.get("icon")).toString(),
                                    Objects.requireNonNull(document.get("categoryName")).toString()
                            ));

                        }
                        myCompleteListener.onComplete();

                    } else {
                        String error = task.getException().getMessage();
                        myCompleteListener.onFailure(error);
                    }
                });
    }

    public static void loadHomeFrag(final MyCompleteListener myCompleteListener) {
        firebaseFirestore.collection("HOMEPAGE").orderBy("index")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot documentHome : task.getResult()) {
                            long viewType = (long) documentHome.get("view_type");
                            if (viewType == 0) {
                                 List<SliderModel> sliderModelList = new ArrayList<>();
                                long no_of_banner = (long) documentHome.get("no_of_banner");
                                for (long x = 1; x < no_of_banner + 1; x++) {
                                    sliderModelList.add(new SliderModel("",documentHome.get("banner_" + x).toString(),
                                            documentHome.get("banner_" + x + "_background").toString()));
                                }
                                homepageModelList.add(new HomepageModel(0, documentHome.getId(), sliderModelList));

                            } else if (viewType == 1) {
                                if (documentHome.get("ad_id") != null) {
                                    homepageModelList.add(new HomepageModel(1, documentHome.getId(), documentHome.getString("ad_id"), "", ""));
                                }

                            } else if (viewType == 2) {
                                List<WishlistModel> viewAllProductList = new ArrayList<>();
                                List<HorizontalProductScrollModel> horizontalproductscrollModelList = new ArrayList<>();
                                ArrayList<String> productsIds =(ArrayList<String>) documentHome.get("products");

                                for (String productId : productsIds){
                                    horizontalproductscrollModelList.add(new HorizontalProductScrollModel(productId, "", "", "", ""));
                                    viewAllProductList.add(new WishlistModel(productId, "", 0.0, 1.0, 1L, "", 0L, 0L, ""));
                                }
                                homepageModelList.add(new HomepageModel(2, documentHome.getId(), documentHome.get("layout_title").toString(), documentHome.get("layout_backgrond").toString(), horizontalproductscrollModelList, viewAllProductList));

                            } else if (viewType == 3) {
                                List<HorizontalProductScrollModel> gridLayoutModelList = new ArrayList<>();
                                List<String> productsIds =(List<String>) documentHome.get("products");

                                for (String productId : productsIds) {
                                    gridLayoutModelList.add(new HorizontalProductScrollModel(productId, "", "", "", ""));
                                }
                                homepageModelList.add(new HomepageModel(3, documentHome.getId(), documentHome.get("layout_title").toString(), documentHome.get("layout_backgrond").toString(), gridLayoutModelList));
                            }
                        }
                        myCompleteListener.onComplete();
                    } else {
                        String error = task.getException().getMessage();
                        myCompleteListener.onFailure(error);
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
    }

    public static void loadCategoryActivity(RecyclerView categoryActivityRecycler, Context context, final int index, String categoryName) {
        firebaseFirestore.collection("CATEGORY")
                .document(categoryName.toUpperCase())
                .collection("CATEGORY_ACTIVITY")
                .orderBy("index")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot documentCate : task.getResult()) {
                            long viewType = (long) documentCate.get("view_type");
                            if (viewType == 0) {
                                List<SliderModel> catsliderModelList = new ArrayList<>();
                                long no_banner = (long) documentCate.get("no_of_banner");
                                for (long x = 1; x < no_banner + 1; x++) {
                                    catsliderModelList.add(new SliderModel("",documentCate.get("banner_" + x).toString(),
                                            documentCate.get("banner_" + x + "_background").toString()));
                                }
                                lists.get(index).add(new HomepageModel(0, documentCate.getId(),catsliderModelList));

                            } else if (viewType == 1) {
                                lists.get(index).add(new HomepageModel(1, documentCate.getId(), "", Objects.requireNonNull(documentCate.getString("strip_ads")),
                                        documentCate.get("stirp_ad_background").toString()));

                            } else if (viewType == 2) {
                                List<WishlistModel> viewAllProductList = new ArrayList<>();
                                List<HorizontalProductScrollModel> horizontalproductscrollModelList = new ArrayList<>();
                                ArrayList<String> productsIds =(ArrayList<String>) documentCate.get("products");

                                for (String productId : productsIds){
                                    horizontalproductscrollModelList.add(new HorizontalProductScrollModel(productId, "", "", "", ""));
                                    viewAllProductList.add(new WishlistModel(productId, "", 0.0, 0.0, 0L, "", 0L, 0L, ""));
                                }
                                lists.get(index).add(new HomepageModel(2, documentCate.getId(), documentCate.get("layout_title").toString(), documentCate.get("layout_backgrond").toString(), horizontalproductscrollModelList, viewAllProductList));

                            } else if (viewType == 3) {
                                List<HorizontalProductScrollModel> gridLayoutModelList = new ArrayList<>();
                                ArrayList<String> productsIds =(ArrayList<String>) documentCate.get("products");
                                for (String productId : productsIds) {
                                    gridLayoutModelList.add(new HorizontalProductScrollModel(productId, "", "", "", ""));
                                }
                                lists.get(index).add(new HomepageModel(3, documentCate.getId(), documentCate.get("layout_title").toString(), documentCate.get("layout_backgrond").toString(), gridLayoutModelList));
                            }
                        }
                        HomepageAdapter adapters = new HomepageAdapter(lists.get(index));
                        categoryActivityRecycler.setAdapter(adapters);
                        adapters.notifyDataSetChanged();
                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
    }

    public static void loadWishList(Context context, Dialog dialog, final boolean loadingFragment) {
        wishLisT.clear();
        firebaseFirestore.collection("USER")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("MY_WISHLIST")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            String pID = documentSnapshot.get("product_ID").toString();
                            wishLisT.add(pID);
                            if (pID.equals(productID)) {
                                if (addToWishListButton != null) {
                                    addToWishListButton.setSupportImageTintList(ContextCompat.getColorStateList(context, R.color.wish));
                                }
                            }
                            if (loadingFragment) {
                                firebaseFirestore.collection("Product_Details").document(pID)
                                        .get().addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                DocumentSnapshot productDoc = task1.getResult();
                                                if (productDoc.exists()) {
                                                    List<String> images = (List<String>) productDoc.get("imageUrls");
                                                    String displayImage = (images != null && !images.isEmpty()) ? images.get(0) : "";
                                                    
                                                    long freeCoupon = 0L;
                                                    long totalRatings = 0L;
                                                    long productPrice = 0L;
                                                    long cutPrice = 0L;
                                                    double starRating = 0.0;

                                                    if (productDoc.get("freeCoupon") instanceof Number) freeCoupon = ((Number) productDoc.get("freeCoupon")).longValue();
                                                    if (productDoc.get("totalRatings") instanceof Number) totalRatings = ((Number) productDoc.get("totalRatings")).longValue();
                                                    if (productDoc.get("productPrice") instanceof Number) productPrice = ((Number) productDoc.get("productPrice")).longValue();
                                                    if (productDoc.get("cutPrice") instanceof Number) cutPrice = ((Number) productDoc.get("cutPrice")).longValue();
                                                    if (productDoc.get("starRating") != null) starRating = Double.parseDouble(productDoc.get("starRating").toString());

                                                    wishlistModelList.add(new WishlistModel(
                                                            pID,
                                                            displayImage,
                                                            freeCoupon,
                                                            starRating,
                                                            totalRatings,
                                                            productDoc.getString("productTitle"),
                                                            productPrice,
                                                            cutPrice,
                                                            productDoc.getString("paymentMethod")
                                                    ));
                                                    wishlistAdapter.notifyDataSetChanged();
                                                }
                                            }
                                        });
                            }
                        }
                        if (!wishLisT.contains(productID)) {
                            if (addToWishListButton != null) {
                                addToWishListButton.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#A8A7A7")));
                            }
                        }
                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                });
    }

    public static void removeFromWishList(String productID, int index, Context context, Dialog dialog) {
        firebaseFirestore.collection("USER")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("MY_WISHLIST")
                .document(productID)
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!wishlistModelList.isEmpty()) {
                            wishlistModelList.remove(index);
                            wishlistAdapter.notifyDataSetChanged();
                        }
                        wishLisT.remove(index);
                        if (addToWishListButton != null) {
                            addToWishListButton.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#A8A7A7")));
                        }
                        Toast.makeText(context, "Product removed successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        if (addToWishListButton != null) {
                            addToWishListButton.setSupportImageTintList(ContextCompat.getColorStateList(context, R.color.wish));
                        }
                    }
                    dialog.dismiss();
                });
    }

    public static void loadCartList(final Context context, final Dialog dialog, final boolean loadingFragment, final TextView badgeCount, final TextView totalAmount) {
        cartLis.clear();
        cartItemModelList.clear();
        firebaseFirestore.collection("USER")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("MY_CART")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            String productID1 = documentSnapshot.getId();
                            cartLis.add(productID1);
                            firebaseFirestore.collection("Product_Details").document(productID1)
                                    .get().addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            DocumentSnapshot doc = task1.getResult();
                                            List<String> images = (List<String>) doc.get("imageUrls");
                                            String firstImage = (images != null && !images.isEmpty()) ? images.get(0) : "";
                                            int index = (!cartItemModelList.isEmpty() && cartItemModelList.get(cartItemModelList.size() - 1).getType() == CartItemModel.CART_TOTAL_AMOUNT_LAYOUT) ? cartItemModelList.size() - 1 : cartItemModelList.size();

                                            cartItemModelList.add(index, new CartItemModel(
                                                    0, productID1, firstImage,
                                                    doc.getString("productTitle"),
                                                    String.valueOf(doc.get("productPrice")),
                                                    String.valueOf(doc.get("cutPrice")),
                                                    String.valueOf(doc.get("freeCoupon")),
                                                    "Order Place next 36 hours",
                                                    doc.getString("paymentMethod"),
                                                    1L,
                                                    Boolean.TRUE.equals(doc.getBoolean("inStock"))
                                            ));

                                            if (!cartLis.isEmpty() && !cartItemModelList.isEmpty()) {
                                                boolean totalLayoutExists = false;
                                                for (CartItemModel model : cartItemModelList) {
                                                    if (model.getType() == CartItemModel.CART_TOTAL_AMOUNT_LAYOUT) {
                                                        totalLayoutExists = true;
                                                        break;
                                                    }
                                                }
                                                if (!totalLayoutExists) {
                                                    cartItemModelList.add(new CartItemModel(CartItemModel.CART_TOTAL_AMOUNT_LAYOUT));
                                                }
                                            }
                                            if (totalAmount != null && totalAmount.getParent() != null) {
                                                ((LinearLayout) totalAmount.getParent()).setVisibility(View.VISIBLE);
                                            }
                                        }
                                    });
                        }
                        updateBadge(badgeCount);
                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                });
    }

    private static void updateBadge(TextView badgeCount) {
        if (badgeCount != null) {
            if (!cartLis.isEmpty()) {
                badgeCount.setVisibility(View.VISIBLE);
                badgeCount.setText(cartLis.size() < 99 ? String.valueOf(cartLis.size()) : "99");
            } else {
                badgeCount.setVisibility(View.INVISIBLE);
            }
        }
    }

    public static void removeFromCartList(int index, Context context, Dialog dialog, TextView totalAmount) {
        final String removeProductID = cartLis.get(index);
        firebaseFirestore.collection("USER")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("MY_CART")
                .document(removeProductID)
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        cartLis.remove(index);
                        if (!cartItemModelList.isEmpty()) {
                            cartItemModelList.remove(index);
                        }
                        if (cartLis.isEmpty()) {
                            if (totalAmount != null) ((LinearLayout) totalAmount.getParent()).setVisibility(View.GONE);
                            cartItemModelList.clear();
                        }
                        Toast.makeText(context, "Removed from cart", Toast.LENGTH_SHORT).show();
                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                });
    }

    public static void loadAddresses(final Context context, final Dialog loadingDialog, final boolean loadingFragment, final boolean openActivityIfEmpty) {
        addressesSelectModelList.clear();
        selectedAddresses = -1;
        firebaseFirestore.collection("USER").document(FirebaseAuth.getInstance().getUid())
                .collection("MY_ADDRESSES")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            if (openActivityIfEmpty) {
                                Intent addAddressIntent = new Intent(context, Add_delivery_address_Activity3.class);
                                addAddressIntent.putExtra("INTENT", "deliveryIntent");
                                context.startActivity(addAddressIntent);
                            }
                        } else {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                boolean selected = Boolean.TRUE.equals(documentSnapshot.getBoolean("selected"));
                                String addressID = documentSnapshot.getId();
                                addressesSelectModelList.add(new AddressesSelectModel(
                                        documentSnapshot.getString("fullName"),
                                        documentSnapshot.getString("mobile"),
                                        documentSnapshot.getString("pinCode"),
                                        documentSnapshot.getString("state"),
                                        documentSnapshot.getString("city"),
                                        documentSnapshot.getString("house"),
                                        documentSnapshot.getString("area"),
                                        selected,
                                        addressID
                                ));
                                if (selected) {
                                    selectedAddresses = addressesSelectModelList.size() - 1;
                                }
                            }
                            if (selectedAddresses == -1 && !addressesSelectModelList.isEmpty()) {
                                selectedAddresses = 0;
                            }
                            if (loadingFragment) {
                                Intent placeIntent = new Intent(context, PLaceActivity3.class);
                                context.startActivity(placeIntent);
                            }
                        }
                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    }
                    loadingDialog.dismiss();
                });
    }

    public static void removeAddress(int index, Context context, Dialog dialog) {
        String addressID = addressesSelectModelList.get(index).getAddressID();
        firebaseFirestore.collection("USER")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("MY_ADDRESSES")
                .document(addressID)
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        addressesSelectModelList.remove(index);
                        Toast.makeText(context, "Address deleted", Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                });
    }

    public static void loadMyOrders(Context context, Dialog dialog, MyOrderItemAdapter myOrderItemAdapter) {
        myOrderItemModelList.clear();
        firebaseFirestore.collection("USER").document(FirebaseAuth.getInstance().getUid()).collection("USER_ORDERS").orderBy("time", Query.Direction.DESCENDING).get()
                .addOnCompleteListener(task ->  {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> userOrders = task.getResult().getDocuments();
                            int totalOrders = userOrders.size();
                            if (totalOrders == 0) {
                                dialog.dismiss();
                                myOrderItemAdapter.notifyDataSetChanged();
                                return;
                            }
                            final int[] loadedOrderCount = {0};
                            for (DocumentSnapshot documentSnapshot : userOrders) {
                                String orderID = documentSnapshot.getString("order id");
                                firebaseFirestore.collection("ORDERS").document(orderID).collection("orderItems").get()
                                        .addOnCompleteListener(innerTask-> {
                                                if (innerTask.isSuccessful()) {
                                                    for (DocumentSnapshot orderItems : innerTask.getResult().getDocuments()) {
                                                        final MyOrderItemModel myOrderItemModel = new MyOrderItemModel(
                                                                orderItems.getString("productID"),
                                                                orderItems.getString("orderID"),
                                                                orderItems.getString("productTitle"),
                                                                orderItems.getString("productImage"),
                                                                orderItems.getString("orderStatus"),
                                                                orderItems.getDate("orderedDate"),
                                                                orderItems.getDate("packedDate"),
                                                                orderItems.getDate("shippedDate"),
                                                                orderItems.getDate("deliveredDate"),
                                                                orderItems.getDate("cancelledDate"),
                                                                orderItems.getString("fullName"),
                                                                orderItems.getString("address"),
                                                                orderItems.getString("mobile"),
                                                                orderItems.getString("pinCode"),
                                                                orderItems.getString("productPrice"),
                                                                orderItems.getString("cutPrice"),
                                                                orderItems.getString("userID"),
                                                                orderItems.getString("paymentMethod"),
                                                                orderItems.getLong("productQuantity")!= null ? orderItems.getLong("productQuantity"):0,
                                                                orderItems.getString("deliveryCharge"),
                                                                Boolean.TRUE.equals(orderItems.getBoolean("cancellationRequested"))
                                                        );
                                                        myOrderItemModelList.add(myOrderItemModel);
                                                    }
                                                    myOrderItemAdapter.notifyDataSetChanged();
                                                    loadedOrderCount[0]++;
                                                    if (loadedOrderCount[0] == totalOrders) {
                                                        myOrderItemModelList.sort((o1, o2) -> {
                                                            Date date1 = o1.getOrderedDate();
                                                            Date date2 = o2.getOrderedDate();
                                                            if (date2 != null && date1 != null) {
                                                                return date2.compareTo(date1);
                                                            }
                                                            return 0;
                                                        });
                                                        myOrderItemAdapter.notifyDataSetChanged();
                                                        dialog.dismiss();
                                                    }
                                                } else {
                                                    String error = innerTask.getException().getMessage();
                                                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                                    loadedOrderCount[0]++;
                                                    if (loadedOrderCount[0] == totalOrders) {
                                                        myOrderItemAdapter.notifyDataSetChanged();
                                                        dialog.dismiss();
                                                    }
                                                }
                                        });
                            }
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                });
    }

    public static void checkNotification(Context context, boolean remove, TextView notifyCount) {
        if (remove) {
            if (registration != null) registration.remove();    } else {
            registration = firebaseFirestore.collection("USER").document(FirebaseAuth.getInstance().getUid())
                    .collection("MY_NOTIFICATIONS")
                    .addSnapshotListener((value, error) -> {
                        if (value != null) {
                            notificationModelList.clear();
                            int unread = 0;
                            for (DocumentSnapshot doc : value.getDocuments()) {
                                notificationModelList.add(0, new NotificationModel(
                                        doc.getId(),
                                        doc.getString("Image"),
                                        doc.getString("Body"),
                                        doc.getBoolean("Read")
                                ));
                                if (!Boolean.TRUE.equals(doc.getBoolean("Read"))) unread++;
                            }
                            if (notifyCount != null) {
                                if (unread > 0) {
                                    notifyCount.setVisibility(View.VISIBLE);
                                    notifyCount.setText(String.valueOf(Math.min(unread, 99)));
                                } else {
                                    notifyCount.setVisibility(View.INVISIBLE);
                                }
                            }
                            if (NotificationActivity.notificationAdapter != null) {
                                NotificationActivity.notificationAdapter.notifyDataSetChanged();
                            }
                        }
                    });
        }
    }

    public static void checkDeviceSession(Context context) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String uid = FirebaseAuth.getInstance().getUid();
            String androidId = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            firebaseFirestore.collection("USER").document(uid)
                    .collection("DEVICES").document(androidId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().exists()) {
                            FirebaseAuth.getInstance().signOut();
                            clearData();
                            Intent intent = new Intent(context, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(intent);
                        }
                    });
        }
    }

    public static void clearData() {
        categoryModelList.clear();
        loadedCategoriesName.clear();
        homepageModelList.clear();
        sliderModelList.clear();
        lists.clear();
        ratingsId.clear();
        myRatings.clear();
        wishLisT.clear();
        wishlistModelList.clear();
        cartLis.clear();
        cartItemModelList.clear();
        myOrderItemModelList.clear();
        notificationModelList.clear();
        selectedAddresses = -1;
    }
}
