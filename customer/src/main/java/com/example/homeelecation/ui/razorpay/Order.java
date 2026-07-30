package com.example.homeelecation.ui.razorpay;

import org.json.JSONObject;

public class Order extends Entity {

  public Order(JSONObject jsonObject) {
    super(jsonObject);
  }

  public String getId(String id){
    return id;
  }

  // ऑर्डर के विशिष्ट फ़ील्ड के लिए गेटर विधियां
//   public String getId() throws JSONException {
//    return this.get();
//  }
   //public long getAmount() { return this.getLong("amount");
 // }
  // ...
}
