# Walkthrough: Electrician Decline Task with Reason

I have implemented the "Decline Task" feature which allows electricians to return an assigned order to the Admin if they are busy or unavailable, along with a reason.

## Changes Made

### 1. Data Model Enhancements
Added `lastDeclinedByBoyID`, `lastDeclinedByBoyName`, and `declineReason` to:
- `OrderModel` (Admin & Delivery)
- `QuickOrderModel` (Admin & Customer)
- `Order` (Delivery fetch model)

### 2. Delivery App: Decline Workflow
- **UI**: Added a "Decline" button next to "Accept Task" in the order list layout.
- **Logic**:
    - When clicked, the electrician selects a reason from a list (*Too Busy, Out of Station, Health Issue, Vehicle Problem, Other*).
    - If "Other" is selected, they can type a custom reason.
    - Upon confirmation, the app updates Firestore by:
        1. Setting `deliveryBoyID` and `deliveryBoyName` to `null` (returning it to the pool).
        2. Saving the decline details (`lastDeclinedByBoyName`, `declineReason`).

### 3. Admin App: Oversight
- **UI**: Updated the order adapters (`OrderAdapter` and `AdminQuickOrderAdapter`) to display decline information.
- Orders that have been declined show a red italic message: *"Declined by: [Electrician Name] ([Reason])"*.
- When the Admin assigns the order to a new electrician, these decline fields are cleared automatically.

## Verification

### Automated Verification
- Code successfully updated in all relevant modules.
- Firestore update logic ensures data consistency.

### Manual Verification Steps
1. Assign an order to an electrician in the Admin app.
2. Open the Delivery app as that electrician.
3. Click **Decline** on the task, select a reason, and submit.
4. Verify the task disappears from the Delivery app.
5. In the Admin app, verify the task now shows the decline reason.
6. Reassign the task to another electrician and verify the decline message disappears.
