package comp5216.sydney.edu.fridgebutler;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;

import android.content.Intent;
import android.util.Log;
import android.widget.ListView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import comp5216.sydney.edu.fridgebutler.Map.MapsActivity;
import comp5216.sydney.edu.fridgebutler.adapter.DataCallBack;
import comp5216.sydney.edu.fridgebutler.adapter.Item;
import comp5216.sydney.edu.fridgebutler.adapter.itemAdapter;
import comp5216.sydney.edu.fridgebutler.recipe.RecommendRecipe;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    int REQUEST_OPEN_EDITLIST = 101;

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    public NavigationView navigationView;
    public FirebaseAuth firebaseAuth;
    public FirebaseFirestore db;
    public String user_id;


    ArrayList <Item> itemList;
    itemAdapter adapter;
    ListView listView;

    ArrayList <Item> expiredFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();

        // drawer layout instance to toggle the menu icon to open
        // drawer and back button to close drawer
        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();


        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        itemList = new ArrayList <Item> ();
        expiredFood = new ArrayList<>();
        adapter = new itemAdapter(this, itemList);


        loadData(new DataCallBack() {
            @Override
            public void onComplete(ArrayList<Item> item) {
                adapter.notifyDataSetChanged();
                setupListViewListener();
            }
        });

        listView = findViewById(R.id.list_item);
        listView.setAdapter(adapter);


    }

    private void setupListViewListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long rowID) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.dialog_delete_title)
                        .setMessage(R.string.dialog_delete_msg)
                        .setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .setNegativeButton(R.string.delete, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String docRef = itemList.get(position).getDocRef();
                                DocumentReference docToDelete = db.collection("ingredients").document(user_id).collection("IngredientList").document(docRef);
                                docToDelete.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                    }
                                })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error deleting document", e);
                                            }
                                        });
                                itemList.remove(position);
                                adapter.notifyDataSetChanged();

                            }
                        });
                builder.create().show();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_logout) {
            firebaseAuth.signOut();
            startActivity(new Intent(getApplicationContext(), Login.class));
        }
        else if(id == R.id.nav_account){
            startActivity(new Intent(getApplicationContext(), ShowProfile.class));
        }
        else if(id == R.id.nav_settings){
            startActivity(new Intent(getApplicationContext(), Setting.class));
        }
        else if(id == R.id.listItem){
            //startActivity(new Intent(getApplicationContext(), EditList.class));
            Intent intent = new Intent(MainActivity.this, EditList.class);
            startActivityForResult(intent, REQUEST_OPEN_EDITLIST);
        }
        else if(id == R.id.nav_map){
            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
        }
        else if(id == R.id.nav_recipe){
            startActivity(new Intent(getApplicationContext(), RecommendRecipe.class));
        }
        drawerLayout.closeDrawers();
        return true;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_OPEN_EDITLIST) {
            if (resultCode == RESULT_OK) {
                String editItemName = data.getExtras().getString("ItemName");
                String editItemTime = data.getExtras().getString("ItemTime");
                String editDocID = data.getExtras().getString("DocID");
                Item item = new Item(editItemName, editItemTime, editDocID);
                itemList.add(item);
                adapter.notifyDataSetChanged();
            }
        }
    }

   /** Load food list from cloud storage, only execute once */
   public void loadData(DataCallBack dataCallBack){
        CollectionReference collectionRef = db.collection("ingredients").document(user_id).collection("IngredientList");
        collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Convert deadline string to int array
                        String deadline = document.get("expiryDate").toString();
                        int[] dateArray = Arrays.stream(deadline.split("/"))
                                .mapToInt(Integer::parseInt)
                                .toArray();

                        // Calculate remaining days
                        int day = dateArray[0];
                        int month = dateArray[1];
                        int year =  dateArray[2];

                        // Calculate remaining days
                        Calendar userDeadline = Calendar.getInstance();
                        userDeadline.set(year,month,day);
                        Long diff = userDeadline.getTimeInMillis() - System.currentTimeMillis();
                        int days = (int)(diff / 86400000); diff -= days * 86400000;
                        String remainingDays = String.format("%d days left", days);

                        if (userDeadline.getTimeInMillis() - System.currentTimeMillis() <= 0) {
                            remainingDays = "OVERDUE";
                        }

                        //Item item= new Item(document.get("Name").toString(), document.get("expiryDate").toString(), document.getId());
                        Item item= new Item(document.get("Name").toString(), remainingDays, document.getId());
                        itemList.add(item);
                    }
                    dataCallBack.onComplete(itemList);
                    checkExpiry();

                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    // Check status of added food, suggest options for expired food
    public void checkExpiry() {
        int flag = 0; // flag to signal if there is food that will expire in 1 day
        for (int i=0; i < itemList.size(); i++) {
            if (itemList.get(i).getTime().equals("OVERDUE")) {
                if (!expiredFood.contains(itemList.get(i))) {
                    expiredFood.add(itemList.get(i));
                }
            }

            if (flag == 0) {
                if (itemList.get(i).getTime().charAt(0) == '1') {
                    // If the food will expire in 1 day, suggest the user to get recipe
                    flag = 1;
                }
            }
        }


        // Ask user if want to remove all the expired food
        if (expiredFood.size() != 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Expired food detected")
                    .setMessage("Do you want to remove all the expired food?")
                    .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            for (int a = 0; a < expiredFood.size(); a++) {
                                String docRef = expiredFood.get(a).getDocRef();
                                DocumentReference docToDelete = db.collection("ingredients").document(user_id).collection("IngredientList").document(docRef);
                                docToDelete.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                    }
                                })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error deleting document", e);
                                            }
                                        });
                            }

                            itemList.removeAll(expiredFood);
                            expiredFood.clear();
                            adapter.notifyDataSetChanged();
                        }
                    });

            builder.create().show();
        }

        // Ask user to handle food which will expire in 1 day
        if (flag == 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Note: food will expire in 1 day")
                    .setMessage("There are food that will expire in 1 day, do you want to check recipe?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Navigate to recipe
                            startActivity(new Intent(getApplicationContext(), RecommendRecipe.class));
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // User cancelled the dialog
                            // Nothing happens
                        }
                    });

            builder.create().show();
        }
    }



}

