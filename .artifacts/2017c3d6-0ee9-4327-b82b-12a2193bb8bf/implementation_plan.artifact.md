# Implementation Plan - Fix CartViewModel access in PLaceActivity3

The user is encountering a compilation error in `PLaceActivity3.java` because it attempts to access a private field `cartViewModel` from `HomeActivity2` as if it were a static member. This violates encapsulation and Java's static/instance access rules.

## Proposed Changes

### [Component] Customer UI

#### [MODIFY] [PLaceActivity3.java](file:///C:/Users/anoop/AndroidStudio/HomeElecation/customer/src/main/java/com/example/homeelecation/ui/place/PLaceActivity3.java)

1.  **Add Import**: Include `com.example.homeelecation.ui.Cart.CartViewModel`.
2.  **Add Field**: Declare `private CartViewModel cartViewModel;`.
3.  **Initialize ViewModel**: In `onCreate`, initialize `cartViewModel` using `ViewModelProvider`.
4.  **Update Adapter Initialization**: Pass the local `cartViewModel` to the `CartAdapter` constructor at line 99.
5.  **Update Verification Logic**: In `onPaymentSuccess`, replace `HomeActivity2.cartViewModel` with the local `cartViewModel` (around line 331).

## Verification Plan

### Automated Tests
- Run `./gradlew :customer:compileDebugJavaWithJavac` to verify that the compilation error is resolved.

### Manual Verification
- Deploy the app and navigate to the Place Order screen to ensure the cart items are displayed correctly using the adapter.
