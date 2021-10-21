package comp5216.sydney.edu.fridgebutler;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditList extends AppCompatActivity {
    DatePicker datepicker;
    EditText addItem;

    String user_id;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list);

        datepicker = findViewById(R.id.datePicker1);
        addItem = findViewById(R.id.editItem);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
    }

    public void addItemOnclick(View view){
        String itemName = addItem.getText().toString();

        int day = datepicker.getDayOfMonth();
        int month = datepicker.getMonth();
        int year =  datepicker.getYear();
        //String expiry = day + "/" + month + "/" + year;

        // Calculate remaining days
        Calendar userDeadline = Calendar.getInstance();
        userDeadline.set(year,month,day);
        Long diff = userDeadline.getTimeInMillis() - System.currentTimeMillis();
        int days = (int)(diff / 86400000); diff -= days * 86400000;
        String expiry = String.format("%d days left", days);

        if (userDeadline.getTimeInMillis() - System.currentTimeMillis() <= 0) {
            expiry = "OVERDUE";
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