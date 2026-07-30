package com.example.deliveryboy.map;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.location.Location; // Naya import
import android.widget.TextView; // Naya import

import com.example.deliveryboy.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ListenerRegistration;
import java.text.DecimalFormat; // Naya import

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private FirebaseFirestore db;
    private String orderId;
    private ListenerRegistration firestoreListener;
    private Marker deliveryBoyMarker;
    private Marker customerMarker;
    private TextView distanceTextView; // Naya variable
    private TextView destinationAddressTextView; // Naya variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        db = FirebaseFirestore.getInstance();
        orderId = getIntent().getStringExtra("ORDER_ID");
        distanceTextView = findViewById(R.id.distanceTextView); // TextView ko initialize kiya
        destinationAddressTextView = findViewById(R.id.destinationAddressTextView);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        trackDeliveryBoyLocation();
        fetchCustomerLocationAndPlaceMarker();
    }

    private void trackDeliveryBoyLocation() {
        if (orderId == null) {
            return;
        }

        DocumentReference orderRef = db.collection("ORDERS").document(orderId);
        firestoreListener = orderRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                GeoPoint geoPoint = snapshot.getGeoPoint("deliveryBoyLocation");
                if (geoPoint != null) {
                    LatLng deliveryBoyLatLng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());

                    if (deliveryBoyMarker == null) {
                        deliveryBoyMarker = googleMap.addMarker(new MarkerOptions()
                                .position(deliveryBoyLatLng)
                                .title("Delivery Boy Location")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    } else {
                        deliveryBoyMarker.setPosition(deliveryBoyLatLng);
                    }

                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(deliveryBoyLatLng, 15));

                    // Doori calculate karein
                    if (customerMarker != null) {
                        calculateAndDisplayDistance();
                    }
                }
            }
        });
    }

    private void fetchCustomerLocationAndPlaceMarker() {
        if (orderId == null) {
            return;
        }

        db.collection("ORDERS").document(orderId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                GeoPoint geoPoint = documentSnapshot.getGeoPoint("Customer Location");
                String address = documentSnapshot.getString("Address");

                if (geoPoint!= null) {
                    LatLng customerLatLng = new LatLng(geoPoint.getLatitude(),geoPoint.getLongitude());

                    if (customerMarker == null) {
                        customerMarker = googleMap.addMarker(new MarkerOptions()
                                .position(customerLatLng)
                                .title("Customer Location")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    } else {
                        customerMarker.setPosition(customerLatLng);
                    }

                    // Address ko TextView mein set kiya
                    if (address != null && !address.isEmpty()) {
                        destinationAddressTextView.setText(address);
                    } else {
                        destinationAddressTextView.setText("Address not available");
                    }

                    // Doori calculate karein
                    if (deliveryBoyMarker != null) {
                        calculateAndDisplayDistance();
                    }
                }
            }
        });
    }

    private void calculateAndDisplayDistance() {
        if (deliveryBoyMarker != null && customerMarker != null) {
            Location deliveryBoyLocation = new Location("DeliveryBoy");
            deliveryBoyLocation.setLatitude(deliveryBoyMarker.getPosition().latitude);
            deliveryBoyLocation.setLongitude(deliveryBoyMarker.getPosition().longitude);

            Location customerLocation = new Location("Customer");
            customerLocation.setLatitude(customerMarker.getPosition().latitude);
            customerLocation.setLongitude(customerMarker.getPosition().longitude);

            float distanceInMeters = deliveryBoyLocation.distanceTo(customerLocation);

            // Convert meters to kilometers for better readability
            double distanceInKm = distanceInMeters / 1000.0;

            // Format to 2 decimal places
            DecimalFormat df = new DecimalFormat("#.##");
            String formattedDistance = df.format(distanceInKm);

            distanceTextView.setText("Distance: " + formattedDistance + " km");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (firestoreListener != null) {
            firestoreListener.remove();
        }
    }
}
