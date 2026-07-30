package com.example.homeadmin.ui.address;

import static com.example.homeadmin.ui.address.Select_Address_Activity3.refreshItem;
import static com.example.homeadmin.ui.place.PLaceActivity3.SELECT_ADDRESS;
import static com.example.homeadmin.ui.profile.My_AccountFragment.MANAGE_ADDRESS;

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

import com.example.homeadmin.R;
import com.example.homeadmin.ui.DbLoadData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AddressesSelectAdapter extends RecyclerView.Adapter<AddressesSelectAdapter.ViewHolder> {

    private List<AddressesSelectModel> addressesSelectModelList;
    private final int MODE;
    private int preSelectPosition;
    private boolean isOptionVisible = false;
    private Dialog loadingDialog;

    public AddressesSelectAdapter(List<AddressesSelectModel> addressesSelectModels, int MODE, Dialog loadingDialog) {
        this.addressesSelectModelList = addressesSelectModels;
        this.MODE = MODE;
        this.preSelectPosition = DbLoadData.selectedAddresses;
        this.loadingDialog = loadingDialog;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_address_itme_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AddressesSelectModel model = addressesSelectModelList.get(position);
        holder.setData(model, position);
    }

    @Override
    public int getItemCount() {
        return addressesSelectModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout constraintLayout;
        TextView fullName, fullAddress, phone;
        RadioButton radioButton;
        Button editButton, addressTypeBtn;
        ImageView optionMenu;
        LinearLayout optionContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fullName = itemView.findViewById(R.id.select_address_itme_Name);
            fullAddress = itemView.findViewById(R.id.select_address_itme_fullAddres);
            phone = itemView.findViewById(R.id.select_address_itme_phoneNu);
            radioButton = itemView.findViewById(R.id.radio1);
            editButton = itemView.findViewById(R.id.select_address_itme_editBtn);
            addressTypeBtn = itemView.findViewById(R.id.textView18);
            constraintLayout = itemView.findViewById(R.id.constraint_row);
            optionMenu = itemView.findViewById(R.id.select_address_itme_opstion_manu);
            optionContainer = itemView.findViewById(R.id.select_address_itme_linerLayout);
        }

        private void setData(AddressesSelectModel model, int position) {
            fullName.setText(model.getFullName());
            fullAddress.setText(model.getHouse() + ", " + model.getArea() + ", " + model.getCity() + ", " + model.getState() + " - " + model.getPinCode());
            phone.setText(model.getMobile());
            
            if (model.getAddressType() != null && !model.getAddressType().isEmpty()) {
                addressTypeBtn.setText(model.getAddressType().toUpperCase());
                addressTypeBtn.setVisibility(View.VISIBLE);
            } else {
                addressTypeBtn.setVisibility(View.GONE);
            }

            if (MODE == SELECT_ADDRESS) {
                radioButton.setVisibility(View.VISIBLE);
                optionMenu.setVisibility(View.GONE);
                radioButton.setChecked(position == DbLoadData.selectedAddresses);

                if (model.isSelected()) {
                    editButton.setVisibility(View.VISIBLE);
                    editButton.setOnClickListener(v -> {
                        Intent addressIntent = new Intent(itemView.getContext(), Add_delivery_address_Activity3.class);
                        addressIntent.putExtra("INTENT", "update_address");
                        addressIntent.putExtra("index", position);
                        itemView.getContext().startActivity(addressIntent);
                    });
                } else {
                    editButton.setVisibility(View.GONE);
                }

                itemView.setOnClickListener(v -> {
                    if (preSelectPosition != position) {
                        int oldPos = preSelectPosition;
                        preSelectPosition = position;
                        DbLoadData.selectedAddresses = position;
                        
                        // Update model selection states locally
                        for (int i = 0; i < addressesSelectModelList.size(); i++) {
                            addressesSelectModelList.get(i).setSelected(i == position);
                        }
                        
                        notifyItemChanged(oldPos);
                        notifyItemChanged(position);
                    }
                });

            } else if (MODE == MANAGE_ADDRESS) {
                radioButton.setVisibility(View.GONE);
                optionMenu.setVisibility(View.VISIBLE);
                optionContainer.setVisibility(View.GONE);

                optionMenu.setOnClickListener(v -> {
                    optionContainer.setVisibility(View.VISIBLE);
                    isOptionVisible = true;
                });

                // Edit Option (First child of optionContainer)
                optionContainer.getChildAt(0).setOnClickListener(v -> {
                    Intent addressIntent = new Intent(itemView.getContext(), Add_delivery_address_Activity3.class);
                    addressIntent.putExtra("INTENT", "update_address");
                    addressIntent.putExtra("index", position);
                    itemView.getContext().startActivity(addressIntent);
                    optionContainer.setVisibility(View.GONE);
                });

                // Delete Option (Second child of optionContainer)
                optionContainer.getChildAt(1).setOnClickListener(v -> {
                    deleteAddressFromFirestore(model, position);
                    optionContainer.setVisibility(View.GONE);
                });

                itemView.setOnClickListener(v -> {
                    if (isOptionVisible) {
                        optionContainer.setVisibility(View.GONE);
                        isOptionVisible = false;
                    }
                });
            }
        }

        private void deleteAddressFromFirestore(AddressesSelectModel model, int position) {
            loadingDialog.show();
            String uid = FirebaseAuth.getInstance().getUid();
            if (uid == null || model.getAddressID() == null) {
                loadingDialog.dismiss();
                return;
            }

            FirebaseFirestore.getInstance().collection("USER").document(uid)
                    .collection("MY_ADDRESSES").document(model.getAddressID())
                    .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            loadingDialog.dismiss();
                            if (task.isSuccessful()) {
                                addressesSelectModelList.remove(position);
                                if (DbLoadData.selectedAddresses == position) {
                                    DbLoadData.selectedAddresses = -1;
                                } else if (DbLoadData.selectedAddresses > position) {
                                    DbLoadData.selectedAddresses--;
                                }
                                notifyDataSetChanged();
                                Toast.makeText(itemView.getContext(), "Address deleted", Toast.LENGTH_SHORT).show();
                            } else {
                                String error = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                                Toast.makeText(itemView.getContext(), error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
