package com.example.homeadmin.ui.address;//package com.example.newcar;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.RadioButton;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.List;
//
//public class SelectAdapter2 extends RecyclerView.Adapter<Address_Adapter.AddresssHolder>{
//
//    private List<AddressesSelectModel> listData;
//    private LayoutInflater inflater;
//    private Context context;
//    private RadioButton rbChecked = null;
//    private int rbPosoition = 0;
//
//    public SelectAdapter2(List<AddressesSelectModel> listData, Context context) {
//        this.listData = listData;
//        this.context = context;
//        this.inflater = LayoutInflater.from(context);
//    }
//
//    @Override
//    public AddresssHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = inflater.inflate(R.layout.select_address_itme_layout 2, parent,false);
//        return new AddresssHolder(view,listData,context);    }
//
//    @Override
//    public void onBindViewHolder(@NonNull Address_Adapter.AddresssHolder holder, int position) {
//
//    }
//
//    @Override
//    public void onBindViewHolder(final AddresssHolder holder, final int position) {
//         item = listData.get(position);
////        holder.address.setText(item.getAddress());
////        holder.state.setText(item.getState());
////        holder.pin_code.setText(item.getPin_code());
////        holder.contact.setText(item.getContact());
//
//        if (holder.selected.isChecked()){
//            rbPosoition = holder.getAdapterPosition();
//            Toast.makeText(context, "" + rbPosoition, Toast.LENGTH_SHORT).show();
//        }
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return listData.size();
//    }
//
//    public class AddresssHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//
//        private TextView address, state, pin_code, contact;
//        private RadioButton selected;
//        private  View container;
//        private List<AddressesSelectModel> selectModelList;
//        private Context context;
//
//        public AddresssHolder(View itemView,List<AddressesSelectModel> listdata,Context context) {
//            super(itemView);
//            this.selectModelList = listdata;
//            this.context = context;
//            itemView.setOnClickListener(this);
////            address = (TextView) itemView.findViewById(R.id.text_address);
////            state = (TextView) itemView.findViewById(R.id.state_text);
////            pin_code = (TextView) itemView.findViewById(R.id.pin_code_text);
////            contact = (TextView) itemView.findViewById(R.id.contact_text);
////            selected = (RadioButton) itemView.findViewById(R.id.select_radioButton);
//            if (rbPosoition == 0 && selected.isChecked())
//            {
//                rbChecked = selected;
//                rbPosoition = 0;
//            }
//            selected.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    RadioButton rb = (RadioButton) v;
//                    int clickedPos = getAdapterPosition();
//                    if (rb.isChecked()){
//                        if(rbChecked != null)
//                        {
//                            rbChecked.setChecked(false);
//                        }
//                        rbChecked = rb;
//                        rbPosoition = clickedPos;
//                    }
//                    else{
//                        rbChecked = null;
//                        rbPosoition = 0;
//                    }
//
//                }
//            });
//
//        }
//
//        @Override
//        public void onClick(View v) {
//            int positenter code hereion = getAdapterPosition();
//        }
//    }
//}
//
