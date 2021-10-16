package comp5216.sydney.edu.fridgebutler;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class Register extends AppCompatActivity {
    EditText nameInput, passwordInput, emailInput, phoneInput;
    Button registerButton;
    TextView logIn;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    ProgressBar progressBar;
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameInput = findViewById(R.id.Name);
        emailInput = findViewById(R.id.emailAddress);
        passwordInput = findViewById(R.id.Password);
        phoneInput = findViewById(R.id.phoneNumber);
        registerButton = findViewById(R.id.Register);
        logIn = findViewById(R.id.login);
        progressBar = findViewById(R.id.progressBar);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public void setRegisterButton(View view){
        String username = nameInput.getText().toString().trim();
        String phone = phoneInput.getText().toString();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if(TextUtils.isEmpty(username)){
            nameInput.setError("Please enter your full name");
        }
        if(TextUtils.isEmpty(email)){
            emailInput.setError("Please enter your email");
            return;
        }
        if(TextUtils.isEmpty(password)){
            passwordInput.setError("Please enter your password");
            return;
        }
        if(TextUtils.isEmpty(phone)){
            phoneInput.setError("Please enter your phone number");
            return;
        }
        if(password.length() < 6){
            passwordInput.setError("Password must have at least 6 characters.");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Map<String, Object> user = new HashMap<>();
                user.put("FullName", username);
                user.put("Email", email);
                user.put("Phone", phone);
                user_id = firebaseAuth.getCurrentUser().getUid();
                DocumentReference documentReference = db.collection("users").document(user_id);
                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: user Profile is created for "+ user_id);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.toString());
                    }
                });
//                db.collection("users")
//                        .add(user)
//                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                            @Override
//                            public void onSuccess(DocumentReference documentReference) {
//                                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Log.w(TAG, "Error adding document", e);
//                            }
//                        });
                if(task.isSuccessful()){
                    Toast.makeText(Register.this, "User Created", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                }
                else{
                    Toast.makeText(Register.this, "Error" + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void setLogIn(View view){
        startActivity(new Intent(getApplicationContext(), Login.class));
    }

}