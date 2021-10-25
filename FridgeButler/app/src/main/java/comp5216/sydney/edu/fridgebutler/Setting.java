package comp5216.sydney.edu.fridgebutler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class Setting extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public void changePassword(View view) {
        EditText resetEmail = new EditText(view.getContext());
        AlertDialog.Builder resetDialog = new AlertDialog.Builder(view.getContext());
        resetDialog.setTitle("Do you want to reset your password?");
        resetDialog.setMessage("Enter your email to receive reset password link.");
        resetDialog.setView(resetEmail);

        resetDialog.setPositiveButton("No", (dialogInterface, i) -> {
        }).setNegativeButton("Yes", (dialogInterface, i) -> {
            String email = resetEmail.getText().toString().trim();
            firebaseAuth.sendPasswordResetEmail(email).addOnSuccessListener(unused -> Toast.makeText(Setting.this, "Link sent to your email.", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(Setting.this, e.getMessage(), Toast.LENGTH_LONG).show());

        });

        resetDialog.create().show();
    }


    public void changePhone(View view) {
        EditText resetPhone = new EditText(view.getContext());
        AlertDialog.Builder resetDialog = new AlertDialog.Builder(view.getContext());
        resetDialog.setTitle("Do you want to change your phone number?");
        resetDialog.setMessage("Enter your new phone number");
        resetDialog.setView(resetPhone);

        user_id = firebaseAuth.getCurrentUser().getUid();

        resetDialog.setPositiveButton("No", (dialogInterface, i) -> {
        }).setNegativeButton("Yes", (dialogInterface, i) -> {
            String phone = resetPhone.getText().toString().trim();

            DocumentReference df = db.collection("users").document(user_id);
            Map<String, Object> updates = new HashMap<>();
            updates.put("Phone", phone);

            df.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(Setting.this, "Phone number updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Setting.this, "Error! Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

        resetDialog.create().show();
    }

}