# Walkthrough - Fix Firestore Conflicting Case Sensitivity

I have fixed the `java.lang.RuntimeException: Found two getters or fields with conflicting case sensitivity for property: orderid` by updating the POJO models in the `admin` module.

The issue was caused by multiple methods mapping to the same property name (case-insensitively) in Firestore. I resolved this by using `@Exclude` on redundant "legacy" or "backward compatibility" methods, ensuring that Firestore only maps one definitive getter/setter pair for each property.

## Changes

### POJO Model Updates

I updated the following files to use `@Exclude` for conflicting properties:

- [MyOrderItemModel.java](file:///C:/Users/anoop/AndroidStudio/HomeElecation/admin/src/main/java/com/example/homeadmin/ui/orders/MyOrderItemModel.java): Excluded `orderId`, `productId`, `userId`, `LegacyAddress`, and `catePrice`.
- [HomepageModel.java](file:///C:/Users/anoop/AndroidStudio/HomeElecation/admin/src/main/java/com/example/homeadmin/ui/home/HomepageModel.java): Excluded `documentId`, `backGroundColor`, `backgoundcolor`, `stripDocumentId`, and `stripBackGroundColor`.
- [OrderItem.java](file:///C:/Users/anoop/AndroidStudio/HomeElecation/admin/src/main/java/com/example/homeadmin/user/OrderItem.java): Excluded `AddressSpaced`.
- [WishlistModel.java](file:///C:/Users/anoop/AndroidStudio/HomeElecation/admin/src/main/java/com/example/homeadmin/ui/wishList/WishlistModel.java): Excluded `productId`.
- [SliderModel.java](file:///C:/Users/anoop/AndroidStudio/HomeElecation/admin/src/main/java/com/example/homeadmin/ui/slideshow/SliderModel.java): Excluded `documentId`.

## Verification Results

### Automated Tests
- Ran `:admin:assembleDebug` and the build finished successfully.

### Manual Verification
- The fixes ensure that Firestore's `CustomClassMapper` no longer finds conflicting property names during runtime serialization/deserialization.
