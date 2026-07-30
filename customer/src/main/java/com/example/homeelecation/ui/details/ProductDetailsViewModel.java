package com.example.homeelecation.ui.details;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.homeelecation.ui.DbLoadData;
import com.example.homeelecation.ui.Cart.CartItemModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ProductDetailsViewModel extends ViewModel {

    private final ProductDetailsRepository repository;
    private final FirebaseAuth auth;
    private final Application application;

    private final MutableLiveData<DocumentSnapshot> _productDetails = new MutableLiveData<>();
    public final LiveData<DocumentSnapshot> productDetails = _productDetails;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    public final LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<String> _statusMessage = new MutableLiveData<>();
    public final LiveData<String> statusMessage = _statusMessage;

    private final MutableLiveData<Integer> _userRating = new MutableLiveData<>();
    public final LiveData<Integer> userRating = _userRating;

    private final MutableLiveData<Boolean> _isWishlisted = new MutableLiveData<>();
    public final LiveData<Boolean> isWishlisted = _isWishlisted;

    private final MutableLiveData<Boolean> _isInCart = new MutableLiveData<>();
    public final LiveData<Boolean> isInCart = _isInCart;

    private final MutableLiveData<ArrayList<CartItemModel>> _navigateToCheckout = new MutableLiveData<>();
    public final LiveData<ArrayList<CartItemModel>> navigateToCheckout = _navigateToCheckout;

    @Inject
    public ProductDetailsViewModel(Application application, ProductDetailsRepository repository, FirebaseAuth auth) {
        this.application = application;
        this.repository = repository;
        this.auth = auth;
        
        _isLoading.setValue(false);
        _isWishlisted.setValue(false);
        _isInCart.setValue(false);
        _userRating.setValue(-1);
        _statusMessage.setValue("");
        _navigateToCheckout.setValue(new ArrayList<>());
        _productDetails.setValue(null);
    }

    /**
     * प्रोडक्ट की पूरी जानकारी और यूजर-स्पेसिफिक डेटा (जैसे रेटिंग, विशलिस्ट) लोड करता है।
     */
    public void loadProductDetails(String productId) {
        _isLoading.setValue(true);
        repository.getProductDetails(productId, new ProductDetailsRepository.OnDataLoadedListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                _productDetails.setValue(documentSnapshot);
                String uid = auth.getUid();
                if (uid != null) {
                    _isWishlisted.setValue(DbLoadData.wishLisT.contains(productId));
                    _isInCart.setValue(DbLoadData.cartLis.contains(productId));
                    fetchUserRating(uid, productId);
                } else {
                    _isLoading.setValue(false);
                }
            }

            @Override
            public void onFailure(String error) {
                _statusMessage.setValue(error);
                _isLoading.setValue(false);
            }
        });
    }

    /**
     * किसी खास प्रोडक्ट के लिए यूजर की रेटिंग लोड करता है।
     */
    private void fetchUserRating(String userId, String productId) {
        repository.getUserRating(userId, productId, new ProductDetailsRepository.OnDataLoadedListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Object ratingObj = documentSnapshot.get("rating");
                    if (ratingObj instanceof Number) {
                        _userRating.setValue(((Number) ratingObj).intValue());
                    } else {
                        _userRating.setValue(0);
                    }

                } else {
                    _userRating.setValue(0);
                }
                _isLoading.setValue(false);
            }

            @Override
            public void onFailure(String error) {
                _statusMessage.setValue(error);
                _isLoading.setValue(false);
            }
        });
    }

    /**
     * प्रोडक्ट की रेटिंग सबमिट करता है और यूजर की रेटिंग भी सेव करता है।
     */
    public void submitRating(String productId, int starPosition, int initialRating) {
        String uid = auth.getUid();
        if (uid == null) return;

        _isLoading.setValue(true);

        Map<String, Object> productRatingUpdate = new HashMap<>();
        productRatingUpdate.put((starPosition + 1) + "_star", FieldValue.increment(1));

        if (initialRating == 0) {
            productRatingUpdate.put("totalRatings", FieldValue.increment(1));
        } else {
            productRatingUpdate.put(initialRating + "_star", FieldValue.increment(-1));
        }

        repository.updateProductRating(productId, productRatingUpdate, new ProductDetailsRepository.OnDataLoadedListener<Void>() {
            @Override
            public void onSuccess(Void data) {
                Map<String, Object> ratingData = new HashMap<>();
                ratingData.put("rating", (long) starPosition + 1);
                ratingData.put("timestamp", FieldValue.serverTimestamp());

                repository.saveUserRating(uid, productId, ratingData, new ProductDetailsRepository.OnDataLoadedListener<Void>() {
                    @Override
                    public void onSuccess(Void data) {
                        loadProductDetails(productId);
                        _statusMessage.setValue("Thank you for rating!");
                    }

                    @Override
                    public void onFailure(String error) {
                        _statusMessage.setValue(error);
                        _isLoading.setValue(false);
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                _statusMessage.setValue(error);
                _isLoading.setValue(false);
            }
        });
    }

    /**
     * विशलिस्ट में प्रोडक्ट को जोड़ता या हटाता है।
     */
    public void toggleWishlist(String productId) {
        String uid = auth.getUid();
        if (uid == null) return;

        _isLoading.setValue(true);
        boolean isCurrentlyWishlisted = Boolean.TRUE.equals(_isWishlisted.getValue());

        if (isCurrentlyWishlisted) {
            repository.removeFromWishlist(uid, productId, new ProductDetailsRepository.OnDataLoadedListener<Void>() {
                @Override
                public void onSuccess(Void data) {
                    _isWishlisted.setValue(false);
                    DbLoadData.wishLisT.remove(productId);
                    _statusMessage.setValue("Removed from wishlist");
                    _isLoading.setValue(false);
                }

                @Override
                public void onFailure(String error) {
                    _statusMessage.setValue(error);
                    _isLoading.setValue(false);
                }
            });
        } else {
            Map<String, Object> productData = new HashMap<>();
            productData.put("product_ID", productId);
            productData.put("timestamp", FieldValue.serverTimestamp());
            repository.updateWishlist(uid, productId, productData, new ProductDetailsRepository.OnDataLoadedListener<Void>() {
                @Override
                public void onSuccess(Void data) {
                    _isWishlisted.setValue(true);
                    DbLoadData.wishLisT.add(productId);
                    _statusMessage.setValue("Added to wishlist");
                    _isLoading.setValue(false);
                }

                @Override
                public void onFailure(String error) {
                    _statusMessage.setValue(error);
                    _isLoading.setValue(false);
                }
            });
        }
    }

    /**
     * कार्ट में प्रोडक्ट को जोड़ता है।
     */
    public void addToCart(String productId) {
        String uid = auth.getUid();
        if (uid == null) return;

        if (Boolean.TRUE.equals(_isInCart.getValue())) {
            _statusMessage.setValue("Already in cart");
            return;
        }

        _isLoading.setValue(true);
        Map<String, Object> cartItem = new HashMap<>();
        cartItem.put("product_ID", productId);
        cartItem.put("quantity", 1L);
        cartItem.put("timestamp", FieldValue.serverTimestamp());

        repository.updateCart(uid, productId, cartItem, new ProductDetailsRepository.OnDataLoadedListener<Void>() {
            @Override
            public void onSuccess(Void data) {
                _isInCart.setValue(true);
                DbLoadData.cartLis.add(productId);
                _statusMessage.setValue("Added to cart");
                _isLoading.setValue(false);
            }

            @Override
            public void onFailure(String error) {
                _statusMessage.setValue(error);
                _isLoading.setValue(false);
            }
        });
    }

    /**
     * "Buy Now" बटन के लिए कार्ट आइटम्स तैयार करता है और चेकआउट के लिए सिग्नल भेजता है।
     */
    public void buyNow(String serviceAmount) {
        if (auth.getUid() == null) return;

        DocumentSnapshot doc = _productDetails.getValue();
        if (doc == null) {
            _statusMessage.setValue("Product details not available.");
            return;
        }

        String productId = doc.getId();
        ArrayList<CartItemModel> cartListForCheckout = new ArrayList<>();

        // 1. Image URLs - Type Safe check
        String firstImage = "";
        Object imagesObj = doc.get("imageUrls");
        if (imagesObj instanceof List) {
            List<?> imagesList = (List<?>) imagesObj;
            if (!imagesList.isEmpty() && imagesList.get(0) instanceof String) {
                firstImage = (String) imagesList.get(0);
            }
        }

        // 2. Numeric & Boolean Values - Hyper-Safe conversion
        long productPrice = 0L;
        Object pPriceObj = doc.get("productPrise");
        if (pPriceObj instanceof Number) productPrice = ((Number) pPriceObj).longValue();

        long productCatPrice = 0L;
        Object cPriceObj = doc.get("productCatPrise");
        if (cPriceObj instanceof Number) productCatPrice = ((Number) cPriceObj).longValue();

        long freeCoupon = 0L;
        Object couponObj = doc.get("freeCoupon");
        if (couponObj instanceof Number) freeCoupon = ((Number) couponObj).longValue();

        boolean inStock = false;
        Object stockObj = doc.get("inStock");
        if (stockObj instanceof Boolean) inStock = (Boolean) stockObj;

        String productTitle = doc.getString("productTitle") != null ? doc.getString("productTitle") : "Unknown Product";


        cartListForCheckout.add(new CartItemModel(0, productId, firstImage,
                productTitle,
                String.valueOf(productPrice),
                String.valueOf(productCatPrice),
                String.valueOf(freeCoupon),
                "Order Place next 36 hours",
                serviceAmount,
                1L,
                inStock));

        cartListForCheckout.add(new CartItemModel(CartItemModel.CART_TOTAL_AMOUNT_LAYOUT));
        
        _navigateToCheckout.setValue(cartListForCheckout);
    }
}
