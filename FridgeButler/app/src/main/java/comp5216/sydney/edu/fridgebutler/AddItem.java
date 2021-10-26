package comp5216.sydney.edu.fridgebutler;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddItem extends AppCompatActivity implements View.OnClickListener {
    DatePicker datepicker;
    EditText addItem;

    String user_id;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    String documentID;
    String remainingDays;
    private static final String[] category = new String[]
            {
                    "Fruits", "Vegetables", "Meat", "seafood", "Dairy"
            };
    private static final String[] time = new String[]
            {
                    "5 days", "3 days", "4 days", "2 days", "7 days"
            };
    private String radioButton;
    private String times;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list);

        datepicker = findViewById(R.id.datePicker1);
        addItem = findViewById(R.id.editItem);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        Button button1 = findViewById(R.id.button);
        Button button2 = findViewById(R.id.button2);

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
    }

//    public void suggestOnclick(View view)
//    {
//        int id = view.getId();
//        if(id == R.id.button2)
//        {
//            radioButton = category[0];
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle("choice the category of your food");
//            builder.setSingleChoiceItems(category, 0, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    radioButton = category[which];
//
//                }
//            });
//            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    Snackbar.make(view,"The tine "+radioButton,Snackbar.LENGTH_SHORT).show();
//
//                }
//            });
//            builder.setNegativeButton("Cancle",null);
//            builder.show();
//    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                String itemName = addItem.getText().toString();

                int day = datepicker.getDayOfMonth();
                int month = datepicker.getMonth();
                int year = datepicker.getYear();
                String expiry = day + "/" + month + "/" + year;

                // Calculate remaining days
                Calendar userDeadline = Calendar.getInstance();
                userDeadline.set(year, month, day);
                Long diff = userDeadline.getTimeInMillis() - System.currentTimeMillis();
                int days = (int) (diff / 86400000);
                diff -= days * 86400000;
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
                break;
            case R.id.button2:

                radioButton = category[0];
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("choice the category of your food");
                builder.setSingleChoiceItems(category, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        radioButton = category[which];
                        times = time[which];

                    }
                });
                builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Snackbar.make(v, "Better last than " + times, Snackbar.LENGTH_SHORT).show();

                    }
                });
                builder.setNegativeButton("Cancle", null);
                builder.show();
                break;

        }
    }
}




