package com.example.deliveryboy.callDailer;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deliveryboy.R;

import java.util.ArrayList;
import java.util.List;

public class ContactsFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView emptyView;

    public ContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        recyclerView = view.findViewById(R.id.contacts_recycler_view);
        emptyView = view.findViewById(R.id.empty_view_contacts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadContacts();

        return view;
    }

    private void loadContacts() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            List<ContactItem> contactList = getContacts();
            if (contactList.isEmpty()) {
                emptyView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                emptyView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                ContactAdapter adapter = new ContactAdapter(contactList);
                recyclerView.setAdapter(adapter);
            }
        } else {
            emptyView.setText("कॉन्टैक्ट्स पढ़ने की अनुमति नहीं है।");
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    private List<ContactItem> getContacts() {
        List<ContactItem> list = new ArrayList<>();
        ContentResolver contentResolver = getContext().getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);

            do {
                String id = cursor.getString(idIndex);
                String name = cursor.getString(nameIndex);
                String phoneNumber = null;

                // फोन नंबर प्राप्त करें
                Cursor phoneCursor = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        new String[]{id},
                        null
                );

                if (phoneCursor != null && phoneCursor.moveToFirst()) {
                    int phoneIndex = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    phoneNumber = phoneCursor.getString(phoneIndex);
                    phoneCursor.close();
                }

                if (phoneNumber != null) {
                    list.add(new ContactItem(name, phoneNumber));
                }
            } while (cursor.moveToNext());

            cursor.close();
        }
        return list;
    }

    // ContactItem डेटा मॉडल
    private static class ContactItem {
        String name;
        String number;

        public ContactItem(String name, String number) {
            this.name = name;
            this.number = number;
        }
    }

    // RecyclerView.Adapter
    private static class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
        private final List<ContactItem> contactList;

        public ContactAdapter(List<ContactItem> contactList) {
            this.contactList = contactList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ContactItem item = contactList.get(position);
            holder.contactName.setText(item.name);
            holder.contactNumber.setText(item.number);
        }

        @Override
        public int getItemCount() {
            return contactList.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView contactName;
            TextView contactNumber;

            ViewHolder(View itemView) {
                super(itemView);
                contactName = itemView.findViewById(R.id.contactName);
                contactNumber = itemView.findViewById(R.id.contactNumber);
            }
        }
    }
}
