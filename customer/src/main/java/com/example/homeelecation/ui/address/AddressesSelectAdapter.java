package com.example.homeelecation.ui.address;

import static com.example.homeelecation.ui.address.Select_Address_Activity3.refreshItem;
import static com.example.homeelecation.ui.place.PLaceActivity3.SELECT_ADDRESS;
import static com.example.homeelecation.ui.profile.My_AccountFragment.MANAGE_ADDRESS;

import android.app.Dialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeelecation.R;
import com.example.homeelecation.ui.DbLoadData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AddressesSelectAdapter extends RecyclerView.Adapter<AddressesSelectAdapter.ViewHolder> {


    private final List<AddressesSelectModel> addressesSelectModelList;
    private final int MODE;
    private int preSelectPosition;
    private final Dialog lodingDialog;
    private boolean refresh = true;


    public AddressesSelectAdapter(List<AddressesSelectModel> addressesSelectModels, int MODE, Dialog lodingDialog) {
        this.addressesSelectModelList = addressesSelectModels;
        this.MODE = MODE;
        preSelectPosition = DbLoadData.selectedAddresses;
        this.lodingDialog = lodingDialog;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_address_itme_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position < addressesSelectModelList.size()) {
            AddressesSelectModel model = addressesSelectModelList.get(position);
            
            String name = model.getFullName() != null ? model.getFullName() : "";
            String mobile = model.getMobileNumber() != null ? model.getMobileNumber() : "";
            String pin = model.getPinCode() != null ? model.getPinCode() : "";
            String state = model.getState() != null ? model.getState() : "";
            String city = model.getCity() != null ? model.getCity() : "";
            String house = model.getHouse() != null ? model.getHouse() : "";
            String area = model.getRoadAreaColony() != null ? model.getRoadAreaColony() : "";
            String type = model.getAddressType() != null ? model.getAddressType() : "";
            boolean selected = model.getSelectAddresses();

            holder.setData(name, mobile, pin, state, city, house, area, selected, type, position);
        }
    }

    @Override
    public int getItemCount() {
        return addressesSelectModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView fullName;
        private final TextView fullAddress;
        private final TextView phone;
        private final RadioButton radioButton;
        private final Button typeBadge;
        private final Button editButton;
        private final ImageView moreIcon;
        private final LinearLayout optionContainer;
        private final TextView editAddressBtn;
        private final TextView removeAddressBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fullName = itemView.findViewById(R.id.select_address_itme_Name);
            fullAddress = itemView.findViewById(R.id.select_address_itme_fullAddres);
            phone = itemView.findViewById(R.id.select_address_itme_phoneNu);
            radioButton = itemView.findViewById(R.id.radio1);
            typeBadge = itemView.findViewById(R.id.textView18);
            editButton = itemView.findViewById(R.id.select_address_itme_editBtn);
            moreIcon = itemView.findViewById(R.id.select_address_itme_opstion_manu);
            if (moreIcon != null) moreIcon.setImageResource(R.drawable.more_vert_24);
            optionContainer = itemView.findViewById(R.id.select_address_itme_linerLayout);
            editAddressBtn = itemView.findViewById(R.id.select_address_itme_linearLayout_Edit);
            removeAddressBtn = itemView.findViewById(R.id.select_address_itme_linearLayout_Remove);
        }


        private void setData(String name, String mobileNumber, String pinCode, String state, String city, String house, String roadAreaColony, boolean selected, String type, final int position) {
            if (fullName != null) fullName.setText(name);
            if (typeBadge != null) {
                if (type != null && !type.isEmpty()) {
                    typeBadge.setVisibility(View.VISIBLE);
                    typeBadge.setText(type);
                } else {
                    typeBadge.setVisibility(View.GONE);
                }
            }
            if (fullAddress != null) {
                String address = (house.isEmpty() ? "" : house + " ") +
                                 (roadAreaColony.isEmpty() ? "" : roadAreaColony + " ") +
                                 (city.isEmpty() ? "" : city + " ") +
                                 (state.isEmpty() ? "" : state + " ") +
                                 pinCode;
                fullAddress.setText(address.trim());
            }
            if (phone != null) phone.setText(mobileNumber);


            if (MODE == SELECT_ADDRESS) {
                if (editButton != null) editButton.setVisibility(View.GONE);
                if (radioButton != null) {
                    radioButton.setVisibility(View.VISIBLE);
                    radioButton.setChecked(position == DbLoadData.selectedAddresses);
                }

                itemView.setOnClickListener(v -> {
                    if (DbLoadData.selectedAddresses != position) {
                        int oldPos = DbLoadData.selectedAddresses;
                        DbLoadData.selectedAddresses = position;
                        refreshItem(oldPos, DbLoadData.selectedAddresses);
                    }
                });

            } else if (MODE == MANAGE_ADDRESS) {
                if (radioButton != null) radioButton.setVisibility(View.GONE);
                if (moreIcon != null) {
                    moreIcon.setVisibility(View.VISIBLE);
                    
                    if (preSelectPosition == position) {
                        if (optionContainer != null) optionContainer.setVisibility(View.VISIBLE);
                    } else {
                        if (optionContainer != null) optionContainer.setVisibility(View.GONE);
                    }

                    moreIcon.setOnClickListener(v -> {
                        if (optionContainer != null) {
                            optionContainer.setVisibility(View.VISIBLE);
                            int oldPos = preSelectPosition;
                            if (preSelectPosition == position) {
                                preSelectPosition = -1;
                            } else {
                                preSelectPosition = position;
                            }

                            if (oldPos != -1) refreshItem(oldPos, oldPos);
                            if (preSelectPosition != -1) refreshItem(preSelectPosition, preSelectPosition);
                        }
                    });
                }

                // UPDATE ADDRESS
                if (editAddressBtn != null) {
                    editAddressBtn.setOnClickListener(v -> {
                        Intent addressIntent = new Intent(itemView.getContext(), Add_delivery_address_Activity3.class);
                        addressIntent.putExtra("INTENT", "update_address");
                        addressIntent.putExtra("index", position);
                        itemView.getContext().startActivity(addressIntent);
                        refresh = false;
                    });
                }

                // REMOVE ADDRESS
                if (removeAddressBtn != null) {
                    removeAddressBtn.setOnClickListener(v -> {
                        if (position < addressesSelectModelList.size()) {
                            lodingDialog.show();
                            String addressID = addressesSelectModelList.get(position).getAddressID();
                            if (addressID != null) {
                                FirebaseFirestore.getInstance().collection("USER")
                                        .document(FirebaseAuth.getInstance().getUid())
                                        .collection("MY_ADDRESSES")
                                        .document(addressID)
                                        .delete()
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                if (position < addressesSelectModelList.size()) {
                                                    addressesSelectModelList.remove(position);
                                                    if (DbLoadData.selectedAddresses == position) {
                                                        DbLoadData.selectedAddresses = -1;
                                                    } else if (DbLoadData.selectedAddresses > position) {
                                                        DbLoadData.selectedAddresses--;
                                                    }
                                                    notifyDataSetChanged();
                                                    Toast.makeText(itemView.getContext(), "Address Removed", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                String err = task.getException() != null ? task.getException().getMessage() : "Delete Failed";
                                                Toast.makeText(itemView.getContext(), err, Toast.LENGTH_SHORT).show();
                                            }
                                            lodingDialog.dismiss();
                                        });
                            } else {
                                lodingDialog.dismiss();
                                Toast.makeText(itemView.getContext(), "Invalid Address ID", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                itemView.setOnClickListener(v -> {
                    if (preSelectPosition != -1) {
                        int old = preSelectPosition;
                        preSelectPosition = -1;
                        refreshItem(old, old);
                    }
                });
            }
        }
    }
}
