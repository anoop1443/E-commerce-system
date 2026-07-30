# Implementation Plan - Fix Firestore RuntimeException (Conflicting Case Sensitivity)

The application is crashing with a `java.lang.RuntimeException: Found two getters or fields with conflicting case sensitivity for property: orderid`. This is caused by having multiple methods in POJO models annotated with `@PropertyName` that result in the same property name when compared case-insensitively (e.g., `orderID` and `orderId`).

## Proposed Changes

I will systematically update all models in the `admin` module to ensure that redundant getters (used for backward compatibility or legacy support) are annotated with `@Exclude` instead of `@PropertyName` if they conflict with an existing property name case-insensitively.

### [Admin Module](file:///C:/Users/anoop/AndroidStudio/HomeElecation/admin)

#### [MODIFY] [MyOrderItemModel.java](file:///C:/Users/anoop/AndroidStudio/HomeElecation/admin/src/main/java/com/example/homeadmin/ui/orders/MyOrderItemModel.java)
- Replace `@PropertyName` with `@Exclude` for:
    - `getProductId()` / `setProductId()`
    - `getOrderId()` / `setOrderId()`
    - `getUserId()` / `setUserId()`
    - `getLegacyAddress()` / `setLegacyAddress()`
    - `getCatePrice()` / `setCatePrice()`

#### [MODIFY] [HomepageModel.java](file:///C:/Users/anoop/AndroidStudio/HomeElecation/admin/src/main/java/com/example/homeadmin/ui/home/HomepageModel.java)
- Replace `@PropertyName` with `@Exclude` for:
    - `getDocumentId()` / `setDocumentId()`
    - `getBackGroundColor()` / `setBackGroundColor()`
    - `getBackgoundcolor()` / `setBackgoundcolor()`
    - `getStripDocumentId()` / `setStripDocumentId()`
    - `getStripBackGroundColor()` / `setStripBackGroundColor()`

#### [MODIFY] [OrderItem.java](file:///C:/Users/anoop/AndroidStudio/HomeElecation/admin/src/main/java/com/example/homeadmin/user/OrderItem.java)
- Replace `@PropertyName("Address")` with `@Exclude` for `getAddressSpaced()` / `setAddressSpaced()`.

#### [MODIFY] [WishlistModel.java](file:///C:/Users/anoop/AndroidStudio/HomeElecation/admin/src/main/java/com/example/homeadmin/ui/wishList/WishlistModel.java)
- Replace `@PropertyName("productId")` with `@Exclude` for `getProductId()` / `setProductId()`.

#### [MODIFY] [SliderModel.java](file:///C:/Users/anoop/AndroidStudio/HomeElecation/admin/src/main/java/com/example/homeadmin/ui/slideshow/SliderModel.java)
- Replace `@PropertyName("documentId")` with `@Exclude` for `getDocumentId()` / `setDocumentId()`.

## Verification Plan

### Automated Tests
- Perform a Gradle build to ensure no compilation errors are introduced.

### Manual Verification
- Deploy the `admin` app and verify that the `FATAL EXCEPTION` no longer occurs when interacting with Firestore-backed features (Orders, Homepage, Wishlist, etc.).
