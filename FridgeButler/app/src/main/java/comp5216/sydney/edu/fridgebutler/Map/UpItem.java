package comp5216.sydney.edu.fridgebutler;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import comp5216.sydney.edu.fridgebutler.Map.MapsActivity;

public class UpItem extends AppCompatActivity {
    String TAG = this.getClass().getSimpleName();
    EditText address, phone;
    ArrayList<String> itemList;
    Button save;

    String user_id;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_up_item);

        address = findViewById(R.id.unit);
        phone = findViewById(R.id.Phone);
        save = findViewById(R.id.Upload);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();



        Bundle extras = getIntent().getExtras();
        itemList = new ArrayList<>();
        if(extras != null){
            itemList = extras.getStringArrayList("itemToUpload");
        }



        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItemOnclick();
            }
        });

    }


    public void addItemOnclick(){
        String addressString = address.getText().toString().trim();
        String phoneNumber = phone.getText().toString().trim();
        Map<String, Object> info = new HashMap<>();
        info.put("Address", addressString);
        info.put("Food", itemList);
        info.put("Phone", phoneNumber);

        DocumentReference docRef = db.collection("donate").document(user_id);
        docRef.set(info).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: address is created "+ user_id);
                Intent intent = new Intent(UpItem.this, MapsActivity.class);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: " + e.toString());
                Toast.makeText(UpItem.this, "Error. Please try again later.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(UpItem.this, MainActivity.class));
            }
        });

    }

//    public void addItemOnclick(){
//        String addressString = address.getText().toString().trim();
//        String phoneNumber = phone.getText().toString().trim();
//        Map<String, Object> address = new HashMap<>();
//        info.put("Address", addressString);
//        info.put("Food", itemList);
//        info.put("Phone", phoneNumber);
//
//        DocumentReference docRef = db.collection("donate").document(user_id);
//        docRef.set(info).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Log.d(TAG, "onSuccess: address is created "+ user_id);
//                Intent intent = new Intent(UpItem.this, MapsActivity.class);
//                startActivity(intent);
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.d(TAG, "onFailure: " + e.toString());
//                Toast.makeText(UpItem.this, "Error. Please try again later.", Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(UpItem.this, MainActivity.class));
//            }
//        });
//    }
}