package com.example.homeelecation.ui.place;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.homeelecation.ui.Cart.CartItemModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class PlaceViewModel extends AndroidViewModel {

    private final PlaceRepository repository;
    private final FirebaseAuth auth;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public final LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public final LiveData<String> error = _error;

    private final MutableLiveData<Boolean> _orderPlaced = new MutableLiveData<>(false);
    public final LiveData<Boolean> orderPlaced = _orderPlaced;

    private final MutableLiveData<Boolean> _paymentUpdateSuccess = new MutableLiveData<>(false);
    public final LiveData<Boolean> paymentUpdateSuccess = _paymentUpdateSuccess;

    private final MutableLiveData<String> _paymentFailed = new MutableLiveData<>();
    public final LiveData<String> paymentFailed = _paymentFailed;

    @Inject
    public PlaceViewModel(Application application, PlaceRepository repository, FirebaseAuth auth) {
        super(application);
        this.repository = repository;
        this.auth = auth;
    }

    /**
     * Firestore में ऑर्डर डेटा (Batch Write) तैयार करके सेव करता है।
     */
    public void placeOrder(String orderId, List<CartItemModel> cartItems, Map<String, Object> shippingInfo, 
                           String razorpayOrderId, String year, String month, Map<String, Object> timeInfo) {
        _isLoading.setValue(true);

        Map<String, Map<String, Object>> itemsMap = new HashMap<>();
        Map<String, Object> totalDetails = new HashMap<>();

        for (CartItemModel model : cartItems) {
            if (model.getType() == CartItemModel.CART_ITEM_LAYOUT) {
                Map<String, Object> details = new HashMap<>(shippingInfo);
                details.put("orderYear", year);
                details.put("orderMonth", month);
                details.put("orderID", orderId);
                details.put("productID", model.getProductID());
                details.put("razorpayOrderID", razorpayOrderId);
                details.put("productImage", model.getProductImage());
                details.put("productTitle", model.getProductTitle());
                details.put("userID", auth.getUid());
                details.put("productQuantity", model.getProductQty());
                details.put("cutPrice", model.getProductCutPrice() != null ? model.getProductCutPrice() : "null");
                details.put("productPrice", model.getProductPrice());
                details.put("orderStatus", "Ordered");
                details.put("paymentMethod", "Pending");
                details.put("dateTime", timeInfo.get("now"));
                details.put("orderedDate", timeInfo.get("now"));
                details.put("packedDate", timeInfo.get("next1"));
                details.put("shippedDate", timeInfo.get("next2"));
                details.put("deliveredDate", timeInfo.get("next4"));
                details.put("cancelledDate", null);
                details.put("deliveryCharge", model.getDeliveryCharges());
                details.put("fullName",shippingInfo.get("fullName"));
                details.put("address", shippingInfo.get("address"));
                details.put("mobile", shippingInfo.get("mobile"));
                details.put("pinCode", shippingInfo.get("pinCode"));

                // itemNetAmount और itemTax कैलकुलेशन
                long price = 0;
                try {
                    price = Long.parseLong(model.getProductPrice());
                } catch (Exception e) {
                }
                long qty = model.getProductQty();
                long extraCharges = 0;
                String chargeStr = model.getDeliveryCharges();
                if (chargeStr != null && !chargeStr.equalsIgnoreCase("free")) {
                    try {
                        extraCharges = Long.parseLong(chargeStr);
                    } catch (Exception e) {

                    }
                }
                long itemTax = 0; // फिलहाल 0 रखा गया है
                long itemNetAmount = (price + extraCharges) * qty + itemTax;

                details.put("itemTax", itemTax);
                details.put("itemNetAmount", itemNetAmount);

                itemsMap.put(model.getProductID(), details);
            } else if (model.getType() == CartItemModel.CART_TOTAL_AMOUNT_LAYOUT) {
                totalDetails.putAll(shippingInfo);
                totalDetails.put("totalItems", model.getTotalItem());
                totalDetails.put("totalItemsPrice", model.getTotalItemPrise());
                totalDetails.put("totalItemsDiscount", model.getTotalItemDiscount());
                totalDetails.put("deliveryCharges", model.getDeliveryCharges());
                totalDetails.put("totalAmount", model.getTotalAmount());
                totalDetails.put("razorpayOrderID", razorpayOrderId);
                totalDetails.put("userID", auth.getUid());
                totalDetails.put("paymentStatus", "Pending");
                totalDetails.put("globalStatus", "Ordered");
                totalDetails.put("orderYear", year);
                totalDetails.put("orderMonth", month);
                totalDetails.put("cancellationRequested", false);
                totalDetails.put("dateTime", timeInfo.get("now"));
            }
        }

        repository.placeOrderBatch(orderId, itemsMap, totalDetails, new PlaceRepository.OnOrderCompleteListener() {
            @Override
            public void onSuccess() {
                _isLoading.postValue(false);
                _orderPlaced.postValue(true);
            }

            @Override
            public void onFailure(String errorMessage) {
                _isLoading.postValue(false);
                _error.postValue(errorMessage);
            }
        });
    }

    /**
     * पेमेंट सफल होने पर ऑर्डर स्टेटस और हिस्ट्री अपडेट करता है।
     */
    public void updatePaymentSuccess(String orderId, String paymentId, String method,  Map<String,Object> historyData) {
        _isLoading.setValue(true);
        Map<String, Object> updates = new HashMap<>();
        updates.put("paymentStatus", "paid");
        updates.put("globalStatus", "Ordered");
        updates.put("paymentID", paymentId);
        updates.put("paymentMethod", method);
        updates.put("paymentTime", historyData.get("dateTime"));


        repository.updateOrderStatus(orderId, updates, new PlaceRepository.OnOrderCompleteListener() {
            @Override
            public void onSuccess() {
                saveHistory(orderId, historyData);
                repository.updateSingleFieldInItems(orderId, method,"Ordered");

            }

            @Override
            public void onFailure(String errorMessage) {
                _isLoading.postValue(false);
                _error.postValue(errorMessage);
            }
        });
    }

    /**
     * पेमेंट फेल होने पर ऑर्डर स्टेटस अपडेट करता है।
     */
    public void updatePaymentFailure(String orderId, String errorMessage, Object timestamp) {
        _isLoading.setValue(true);
        Map<String, Object> updates = new HashMap<>();
        updates.put("paymentStatus", "payment failed");
        updates.put("globalStatus", "Payment Failed");
        updates.put("Payment_error_msg", errorMessage);
        updates.put("dateTimeError", timestamp);
        String method = "payment failed";

        repository.updateOrderStatus(orderId, updates, new PlaceRepository.OnOrderCompleteListener() {
            @Override
            public void onSuccess() {
                Map<String, Object> historyData = new HashMap<>();
                historyData.put("orderID", orderId);
                historyData.put("paymentMethod", method);
                historyData.put("dateTime", FieldValue.serverTimestamp());

                _isLoading.postValue(false);
                _paymentFailed.postValue(errorMessage);
                repository.updateSingleFieldInItems(orderId,method,"Payment Failed");
                saveHistory(orderId,historyData);
            }

            @Override
            public void onFailure(String errorMsg) {
                _isLoading.postValue(false);
                _error.postValue(errorMsg);
            }
        });
    }

    private void saveHistory(String orderId, Map<String, Object> historyData) {
        String uid = auth.getUid();
        if (uid == null) return;
        repository.saveUserOrderHistory(uid, orderId, historyData, new PlaceRepository.OnOrderCompleteListener() {
            @Override
            public void onSuccess() {
                _isLoading.postValue(false);
                _paymentUpdateSuccess.postValue(true);
            }

            @Override
            public void onFailure(String errorMessage) {
                _isLoading.postValue(false);
                _error.postValue(errorMessage);
            }
        });
    }
}
