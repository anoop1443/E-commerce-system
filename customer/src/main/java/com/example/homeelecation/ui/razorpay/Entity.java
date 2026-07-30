package com.example.homeelecation.ui.razorpay;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;


public abstract class Entity {

  String CREATED_AT = "created_at";
      String CAPTURED_AT = "captured_at";

  private final JSONObject modelJson;

    public Entity(JSONObject jsonObject) {
    this.modelJson = jsonObject;
  }


  // In Entity.java
//  public <T> T get(String key) throws JSONException {
//    if (!has(key) || modelJson.isNull(key)) { // Check for null in JSONObject too
//      return null;
//    }
//
//    // Handle Date for timestamps
//    if (key.equals(CREATED_AT) || key.equals(CAPTURED_AT)) {
//      // Ensure the value is indeed a Long for time conversion
//      if (modelJson.opt(key) instanceof Long) {
//        return (T) new Date(modelJson.getLong(key) * 1000);
//      }
//      return null; // Or throw an exception if type is unexpected
//    }
//
//    Object value = modelJson.get(key);
//    // Direct casting after checking is generally more reliable than getClass().cast(value)
//    // However, ensuring the return type T matches the actual type in JSON is crucial at compile time.
//    // A more robust SDK would provide specific getters for common types (getString, getInt, getBoolean, etc.)
//    // For example:
//    // if (value instanceof String) return (T) value;
//    // if (value instanceof Integer) return (T) value;
//    // ... and so on.
//
//    // As a generic getter, this remains an unchecked cast.
//    // The user of this method needs to be careful with the expected type T.
//    return (T) value;
//  }

  public <T> T get(String key) throws JSONException {
    // Return null if key not in JSONObject
    if (!has(key)) {
      return null;
    }
    // Return Date for timestamps
      String CREATED_AT = "created_at";
      String CAPTURED_AT = "captured_at";
      if (key.equals(CREATED_AT) || key.equals(CAPTURED_AT)) {
      return (T) new Date( modelJson.getLong(key) * 1000);
    }
    Object value = modelJson.get(key);
      return (T) value.getClass().cast(value);
  }

  public JSONObject toJson() {
    return modelJson;
  }

  public boolean has(String key) {
    return modelJson.has(key);
  }

  public String toString() {
    return modelJson.toString();
  }
}
