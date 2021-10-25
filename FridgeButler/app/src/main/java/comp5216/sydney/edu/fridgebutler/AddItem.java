package comp5216.sydney.edu.fridgebutler;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddItem extends AppCompatActivity {
    DatePicker datepicker;
    EditText addItem;
    Button saveButton;

    String user_id;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    String remainingDays;
    String documentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list);

        datepicker = findViewById(R.id.datePicker1);
        addItem = findViewById(R.id.editItem);
        saveButton = findViewById(R.id.saveItem);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String itemName = addItem.getText().toString();

                int day = datepicker.getDayOfMonth();
                int month = datepicker.getMonth() +1;
                int year =  datepicker.getYear();
                String expiry = day + "/" + month + "/" + year;

                //Change here
                Calendar userDeadline = Calendar.getInstance();
                userDeadline.set(year,month-1,day);
                Long diff = userDeadline.getTimeInMillis() - System.currentTimeMillis();
                int days = (int)(diff / 86400000); diff -= days * 86400000;
                remainingDays = String.format("%d days left", days);

                if (userDeadline.getTimeInMillis() - System.currentTimeMillis() <= 0) {
                    remainingDays = "OVERDUE";
                }


                Map<String, Object> itemsList = new HashMap<>();
                itemsList.put("expiryDate", expiry);
                itemsList.put("Name", itemName);

                CollectionReference df = db.collection("ingredients").document(user_id).collection("IngredientList");
                df.add(itemsList)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d(TAG, "Add new Item: " + documentReference.getId());
                                    documentID = documentReference.getId();
                                    // Prepare data intent for sending it back
                                    Intent data = new Intent();

                                    // Pass relevant data back as a result
                                    data.putExtra("ItemName", itemName);
                                    data.putExtra("ItemTime", remainingDays);
                                    data.putExtra("DocID", documentID);

                                    // Activity finishes OK, return the data
                                    setResult(RESULT_OK, data);
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error adding document", e);
                                }
                            });
            }
        });
    }

    public void addItemOnclick(View view){
        String itemName = addItem.getText().toString();

        int day = datepicker.getDayOfMonth();
        int month = datepicker.getMonth() + 1;
        int year =  datepicker.getYear();
        String expiry = day + "/" + month + "/" + year;

        boolean uploaded = false;

        Map<String, Object> itemsList = new HashMap<>();
        itemsList.put("expiryDate", expiry);
        itemsList.put("Name", itemName);
        itemsList.put("Uploaded", uploaded);

        CollectionReference df = db.collection("ingredients").document(user_id).collection("IngredientList");
        df.add(itemsList)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "Add new Item: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}