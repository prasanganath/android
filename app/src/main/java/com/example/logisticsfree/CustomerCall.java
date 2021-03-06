package com.example.logisticsfree;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.logisticsfree.Common.Common;
import com.example.logisticsfree.Common.Utils;
import com.example.logisticsfree.Remote.IFCMService;
import com.example.logisticsfree.Remote.IGoogleAPI;
import com.example.logisticsfree.models.Trip;
import com.example.logisticsfree.trip.TripProcessing;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerCall extends AppCompatActivity {
    private final String TAG = "CustomerCall";

    TextView txtTime, txtAddress, txtDistance, txtDate, txtCompany;
    MediaPlayer mediaPlayer;
    Button btnCancel, btnAccept;
    FirebaseFirestore fs;
    FirebaseUser mUser;

    IGoogleAPI mService;
    IFCMService mFCMService;

    String customerId;
    double lat, lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_call);

        fs = FirebaseFirestore.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        mService = Common.getGoogleAPI();
        mFCMService = Common.getFCMService();

        txtTime = findViewById(R.id.txtTime);
        txtDate = findViewById(R.id.txtDate);
//        txtDistance = findViewById(R.id.txtDistance);
        txtCompany = findViewById(R.id.txtCompany);

        btnAccept = findViewById(R.id.btnAccept);
        btnCancel = findViewById(R.id.btnDecline);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(customerId))
                    cancelBooking(customerId);
            }
        });

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(CustomerCall.this, WarehouseMap.class);
//                intent.putExtra("lat", lat);
//                intent.putExtra("lng", lng);
//                intent.putExtra("customerId", customerId);
//
//                ng app will create the order Document at /order-requests/CompnayID/order-requests/TruckID
//                after accept the order move it(orderDocument)
//                from  /order-requests/companyID/order-requests/TruckID
//                to    /ordered-trucks/TruckID/ordered-trucks/companyID
                moveRequestToOrder(customerId);
//                startActivity(intent);
                finish();
            }
        });

        mediaPlayer = MediaPlayer.create(this, R.raw.ringtone);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        if (getIntent() != null) {
            lat = Double.parseDouble(getIntent().getStringExtra("lat"));
            lng = Double.parseDouble(getIntent().getStringExtra("lng"));
            customerId = getIntent().getStringExtra("customerID");
            System.out.println("CustomerID : " + customerId);

            txtTime.setText(getIntent().getStringExtra("time"));
            txtDate.setText(getIntent().getStringExtra("date"));

            FirebaseFirestore.getInstance().document("users/" + customerId)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                    txtCompany.setText((String) task.getResult().);
                    txtCompany.setText(task.getResult().getString("name"));
                }
            });
//            getDirection(lat, lng);
        }
    }

    private void moveRequestToOrder(String customerID) {
        String from = "order-requests/" + customerID + "/order-requests/" + mUser.getUid();
        String to = "trips/";

        final DocumentReference fromDoc = fs.document(from);
        final DocumentReference toDoc = fs.collection(to).document();

        final Map<String, Object> data = new HashMap<>();
        data.put("driverID", mUser.getUid());
        data.put("tripID", toDoc.getId());
        data.put("status", 0);
        data.put("active", false);

        fromDoc.set(data, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) return;

                Utils.moveFirestoreDocument(fromDoc, toDoc);
            }
        });
    }

//    public void moveFirestoreDocument(final DocumentReference fromPath, final DocumentReference toPath) {
//        System.out.println(fromPath.getPath() + " " + toPath.getPath());
//        fromPath.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document != null) {
////
////                        Map<String, Object> data = new HashMap<>();
////                        String vid = document.get("truck.truck.vid").toString();
////                        data.put(vid, document.getData());
//
//                        toPath.set(document.getData(), SetOptions.merge())
//                                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void aVoid) {
//                                        Log.d(TAG, "DocumentSnapshot successfully written!");
//                                        fromPath.delete()
//                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                    @Override
//                                                    public void onSuccess(Void aVoid) {
//                                                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
//                                                    }
//                                                })
//                                                .addOnFailureListener(new OnFailureListener() {
//                                                    @Override
//                                                    public void onFailure(@NonNull Exception e) {
//                                                        Log.w(TAG, "Error deleting document", e);
//                                                    }
//                                                });
//                                    }
//                                })
//                                .addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        Log.w(TAG, "Error writing document", e);
//                                    }
//                                });
//                    } else {
//                        Log.d(TAG, "No such document");
//                    }
//                } else {
//                    Log.d(TAG, "get failed with ", task.getException());
//                }
//            }
//        });
//    }

    private void cancelBooking(String customerId) {
//        Token token = new Token(customerId);
        System.out.println("cancel" + customerId);

        DocumentReference requestPath = fs.document("order-requests/" + customerId + "/order-requests/" + mUser.getUid());
        final DocumentReference driverRef = fs.document("drivers/" + mUser.getUid());
        requestPath.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        driverRef.update("available", true);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });

//        Notification notification = new Notification("Cancel","Driver has cancelled your request");
//        Sender sender = new Sender(token.getToken(),notification);

//        Map<String, String> content = new HashMap<>();
//        content.put("title", "Cancel");
//        content.put("message", "Driver has cancelled your request");
//        DataMessage dataMessage = new DataMessage(token.getToken(), content);
//
//        mFCMService.sendMessage(dataMessage)
//                .enqueue(new Callback<FCMResponse>() {
//                    @Override
//                    public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
//                        if (response.body().success == 1) {
//                            Toast.makeText(CustomerCall.this, "Cancelled", Toast.LENGTH_SHORT).show();
//                            finish();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<FCMResponse> call, Throwable t) {
//
//                    }
//                });
    }

    private void getDirection(double lat, double lng) {
        System.out.println("Debug1 " + lat + "," + lng);
        String requestApi;
        try {
            requestApi = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "mode=driving&" +
                    "transit_routing_preferences=less_driving&" +
                    "origin=" + Common.mLastLocation.getLatitude() + "," + Common.mLastLocation.getLongitude() + "&" +
                    "destination=" + lat + "," + lng + "&" +
                    "key=" + getResources().getString(R.string.google_direction_api);
            Log.d("checkLog", requestApi);
            mService.getPath(requestApi)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body());

                                JSONArray routes = jsonObject.getJSONArray("routes");
                                JSONObject object = routes.getJSONObject(0);
                                JSONArray legs = object.getJSONArray("legs");
                                JSONObject legsObject = legs.getJSONObject(0);
                                JSONObject distance = legsObject.getJSONObject("distance");
                                txtDistance.setText(distance.getString("text"));

                                JSONObject time = legsObject.getJSONObject("duration");
                                txtTime.setText(time.getString("text"));

                                String address = legsObject.getString("end_address");
                                txtAddress.setText(address);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(CustomerCall.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        mediaPlayer.release();
        super.onStop();
    }

    @Override
    protected void onPause() {
        mediaPlayer.pause();
        super.onPause();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        mediaPlayer.start();
    }
}
