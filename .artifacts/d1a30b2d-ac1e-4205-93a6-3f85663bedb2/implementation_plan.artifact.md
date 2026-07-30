# Implementation Plan: Decline Task Feature for Electricians

The goal is to allow electricians (delivery boys) to decline an assigned task if they are busy or unavailable. When an order is declined, the electrician must provide a reason. The order should then be returned to the "Unassigned" pool for the Admin to reassign, but the Admin should be able to see who declined it and why.

## Proposed Changes

### 1. Data Model Updates
- **[MODIFY] [OrderModel.java](file:///C:/Users/anoop/AndroidStudio/HomeElecation/admin/src/main/java/com/example/homeadmin/ui/orders/OrderModel.java)** and its counterparts in `delivery` and `customer` (if used).
    - Add fields: `lastDeclinedByBoyID`, `lastDeclinedByBoyName`, `declineReason`.

### 2. Delivery App: Decline Workflow
- **[MODIFY] [item_order.xml](file:///C:/Users/anoop/AndroidStudio/HomeElecation/delivery/src/main/res/layout/item_order.xml)**
    - Add a "Decline Task" button next to the "Accept Task" button.
- **[MODIFY] [OrderAdapter.java](file:///C:/Users/anoop/AndroidStudio/HomeElecation/delivery/src/main/java/com/example/deliveryboy/orderfech/OrderAdapter.java)**
    - Implement the `Decline` button click listener.
    - Show an `AlertDialog` with a predefined list of reasons (e.g., "Too Busy", "Out of Station", "Health Issue", "Vehicle Problem") and an option for a custom reason.
    - Upon confirmation:
        - Update Firestore:
            - Set `deliveryBoyID = null` and `deliveryBoyName = null`.
            - Set `lastDeclinedByBoyID`, `lastDeclinedByBoyName`, and `declineReason`.
            - Ensure the `orderStatus` (or `globalStatus`) remains such that the Admin can reassign it.

### 3. Admin App: Visibility
- **[MODIFY] [OrderAdapter.java](file:///C:/Users/anoop/AndroidStudio/HomeElecation/admin/src/main/java/com/example/homeadmin/ui/orders/OrderAdapter.java)**
    - Update the UI to show if an order was recently declined.
    - Display the name of the boy who declined and the reason.
    - Highlight such orders (e.g., with a red border or "Declined" badge) so the Admin knows immediate action is needed.

## Verification Plan

### Automated Tests
- N/A (UI and Firestore integration verification).

### Manual Verification
1. **Admin App**: Assign an order to "Electrician A".
2. **Delivery App (Electrician A)**: See the order in the list. Click "Decline Task".
3. **Delivery App**: Select a reason (e.g., "Too Busy") and confirm.
4. **Delivery App**: Verify the order disappears from Electrician A's list.
5. **Admin App**: Verify the order now shows as "Unassigned" but displays "Declined by: Electrician A (Reason: Too Busy)".
6. **Admin App**: Reassign the order to "Electrician B" and verify it moves to their list.
