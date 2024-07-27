package com.example.homeelecation.ui.address;

import static com.example.homeelecation.ui.address.Select_Address_Activity3.refreshItem;
import static com.example.homeelecation.ui.place.PLaceActivity3.SELECT_ADDRESS;
import static com.example.homeelecation.ui.profile.My_AccountFragment.MANAGE_ADDRESS;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeelecation.R;
import com.example.homeelecation.ui.DbLoadData;

import java.util.List;

public class AddressesSelectAdapter extends RecyclerView.Adapter<AddressesSelectAdapter.ViewHolder> {


    List<AddressesSelectModel> selectModels;
    int mSelectedItem = 0;
    private int MODE;
    private int preSelectPosition;


    public AddressesSelectAdapter(List<AddressesSelectModel> addressesSelectModels, int MODE) {
        this.selectModels = addressesSelectModels;
        this.MODE = MODE;

        preSelectPosition = DbLoadData.selectedAddresses;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_address_itme_layout,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String fullName = selectModels.get(position).getFullName();
        String fullAddress = selectModels.get(position).getFullAddress();
        String phone = selectModels.get(position).getPhone();
        boolean select = selectModels.get(position).getSelectAddresses();

        holder.radioButton.setChecked(position==preSelectPosition);

        // holder.radioButton.setChecked(position == mSelectedItem);


      holder.setDat(fullName,fullAddress,phone,select,position);
    }

    @Override
    public int getItemCount() {
        return selectModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout constraintLayout;
        TextView fullName,fullAddress,phone;
        RadioButton radioButton;
        Button editButton;
        ImageView optionManu,imageView;
        LinearLayout openOption;

        View.OnClickListener onClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fullName = itemView.findViewById(R.id.select_address_itme_Name);
            fullAddress = itemView.findViewById(R.id.select_address_itme_fullAddres);
            phone = itemView.findViewById(R.id.select_address_itme_phoneNu);
            radioButton = itemView.findViewById(R.id.radio1);
            editButton= itemView.findViewById(R.id.select_address_itme_editBtn);
            constraintLayout = itemView.findViewById(R.id.constraint_row);
            imageView = itemView.findViewById(R.id.select_address_itme_opstion_manu);
           // optionManu = itemView.findViewById(R.id.select_address_itme_opstion_manu);
            openOption = itemView.findViewById(R.id.select_address_itme_linerLayout);
            imageView.setImageResource(R.drawable.more_vert_24);


        }


        private void setDat(String name,String address,String noPhone,boolean select,int position) {
            fullName.setText(name);
            fullAddress.setText(address);
            phone.setText(noPhone);


            if (MODE == SELECT_ADDRESS){

                radioButton.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.INVISIBLE);


                if (select) {

                    editButton.setVisibility(View.VISIBLE);
                    preSelectPosition = position;
                }else {

                    editButton.setVisibility(View.GONE);


                }
                itemView.setOnClickListener( new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (preSelectPosition != position) {
                            //preSelectPosition = getAdapterPosition();
                            notifyItemRangeChanged(0,selectModels.size());
                            selectModels.get(position).setSelectAddresses(true);
                            selectModels.get(preSelectPosition).setSelectAddresses(false);
                            refreshItem(preSelectPosition, position);
                            preSelectPosition = position;
                            DbLoadData.selectedAddresses = position;

                        }
                    }
                });




            }else if (MODE == MANAGE_ADDRESS){
                openOption.setVisibility(View.GONE);
                radioButton.setVisibility(View.INVISIBLE);
                editButton.setVisibility(View.INVISIBLE);
//                optionManu.setVisibility(View.VISIBLE);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openOption.setVisibility(View.VISIBLE);
                        refreshItem(preSelectPosition,preSelectPosition);
                        preSelectPosition = position;
                    }
                });
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        refreshItem(preSelectPosition,preSelectPosition);
                        preSelectPosition = -1;

                    }
                });



            }


//            itemView.setOnClickListener(onClickListener);
////            // use from here*********************
//            View.OnClickListener l = new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mSelectedItem = getAdapterPosition();
//                    notifyItemRangeChanged(0,selectModels.size());

           //     }
      //      };

//            itemView.setOnClickListener(l);
            //  radioButton.setOnClickListener(l);


              }

    }
}
