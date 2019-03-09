package piktoclean.com.gtdriver;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UserActivity extends AppCompatActivity {
    ArrayList<DataModel> dataModels;
    ListView listView;
    private static CustomAdapter adapter;
    private static final String TAG = "UserActivity";

    private FirebaseFirestore db;
    CollectionReference documentReference;
    String Dphone,address;
    Uri picuri;
    int count=0;
    ArrayList<Point> arr = new ArrayList<Point>();
    Button mGet;
    Point start,end;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        SharedPreferences mpreference = getSharedPreferences("piktoclean.com.gtdriver", Context.MODE_PRIVATE);
        Dphone= mpreference.getString("user","");
        db = FirebaseFirestore.getInstance();
        documentReference = db.collection("GTdriver");
        listView = (ListView) findViewById(R.id.list);
        CollectionReference gtdriver = db.collection("GTdriver");
        gtdriver.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot d : task.getResult()) {
                        if(d.getString("Dphone").equals(Dphone)){
                            start=new Point(d.getGeoPoint("Spoint").getLatitude(),d.getGeoPoint("Spoint").getLongitude());
                            end=new Point(d.getGeoPoint("Epoint").getLatitude(),d.getGeoPoint("Epoint").getLongitude());
                            break;
                        }
                    }
                }
            }
        });
        Log.d("sdf",Dphone);
        dataModels = new ArrayList<>();
        mGet = findViewById(R.id.nav);
        CollectionReference Garbage = db.collection("Garbage");
        Garbage.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot d : task.getResult()) {
                        if(d.getString("Dphone").equals(Dphone)){
                            Geocoder geocoder;
                            List<Address> addresses;
                            geocoder = new Geocoder(UserActivity.this, Locale.getDefault());

                            try {
                                addresses = geocoder.getFromLocation(d.getGeoPoint("Location").getLatitude(), d.getGeoPoint("Location").getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                                address = addresses.get(0).getAddressLine(0)+","+addresses.get(0).getLocality();
                                arr.add(new Point(d.getGeoPoint("Location").getLatitude(),d.getGeoPoint("Location").getLongitude()));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            FirebaseStorage storage = FirebaseStorage.getInstance();

                            StorageReference storageRef = storage.getReferenceFromUrl("gs://piktoclean.appspot.com/images/").child(d.getId());
                            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    picuri=uri;
                                    //Handle whatever you're going to do with the URL here
                                }
                            });
                            dataModels.add(new DataModel(d.getString("Uname"), d.getString("Uphone"), address,picuri));
                            count++;
                        }
                        adapter= new CustomAdapter(dataModels,UserActivity.this);
                        listView.setAdapter(adapter);

                    }
                }
            }
        });
        mGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ghf",""+arr.size());
                String waypoint="";
                for(int i=0;i<arr.size();i++) {
                    if (i == 0) {
                        waypoint +=  arr.get(i).getLat() + "," + arr.get(i).getLon();
                    } else {
                        waypoint += "|" + arr.get(i).getLat() + "," + arr.get(i).getLon();
                    }
                }
                Uri gmmIntentUri = Uri.parse("https://www.google.com/maps/dir/?api=1&origin="+start.getLat()+","+start.getLon()+"&destination="+end.getLat()+","+end.getLon()+"&waypoints="+waypoint+"&travelmode=driving");
                Intent intent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                intent.setPackage("com.google.android.apps.maps");
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    try {
                        Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        startActivity(unrestrictedIntent);
                    } catch (ActivityNotFoundException innerEx) {
                        Toast.makeText(UserActivity.this, "Please install a maps application", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });


    }
}

