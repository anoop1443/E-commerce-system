package com.example.homeelecation.ui.razorpay;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class OrderClient extends ApiClient {

  public OrderClient(String auth) {
    super(auth);
  }

  public Order create(JSONObject request) throws RazorpayException, JSONException {
    return post(Constants.VERSION, Constants.ORDER_CREATE, request);
  }

  public List<Order> fetchAll() throws RazorpayException, JSONException {
    return fetchAll(null);
  }

  public List<Order> fetchAll(JSONObject request) throws RazorpayException, JSONException {
    return getCollection(Constants.VERSION, Constants.ORDER_LIST, request);
  }

  public Order fetch(String id) throws RazorpayException, JSONException {
    return get(Constants.VERSION, String.format(Constants.ORDER_GET, id), null);
  }

  public List<Payment> fetchPayments(String id) throws RazorpayException, JSONException {
    return getCollection(Constants.VERSION, String.format(Constants.ORDER_PAYMENT_LIST, id), null);
  }

  public Order edit(String id, JSONObject request) throws RazorpayException, JSONException {
    return patch(Constants.VERSION, String.format(Constants.ORDER_EDIT, id), request);
  }

  public Order viewRtoReview(String id) throws RazorpayException, JSONException {
    return post(Constants.VERSION, String.format(Constants.VIEW_RTO, id), null);
  }

  public Order editFulfillment(String id, JSONObject request) throws RazorpayException, JSONException {
    return post(Constants.VERSION, String.format(Constants.FULFILLMENT, id), request);
  }
}
