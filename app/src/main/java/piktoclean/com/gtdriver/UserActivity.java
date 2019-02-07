package piktoclean.com.gtdriver;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class UserActivity extends AppCompatActivity {

    private static final String TAG = "UserActivity";

    private FirebaseFirestore db;
    DocumentReference documentReference;

    Button mGet;
    TextView tvName,tvPhone,tvStart,tvEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        db = FirebaseFirestore.getInstance();
        documentReference = db.collection("GTdriver").document();

        mGet = findViewById(R.id.btn1);
        tvName = findViewById(R.id.tx1);
        tvPhone = findViewById(R.id.tx2);
//        tvStart = findViewById(R.id.tx3);
//        tvName = findViewById(R.id.tx4);
/*
     db.collection("GTdriver").get()
             .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                 @Override
                 public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                     if(!queryDocumentSnapshots.isEmpty()){
                         List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                         for(DocumentSnapshot d : list){
                             GTDriver gt = d.toObject(Display.class);

                         }
                     }
                 }
             })*/

        mGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("GTdriver").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        for (DocumentSnapshot ds : documentSnapshots) {
                            String firstname;
                            Integer number;

                            firstname = ds.getString("Dname");
                            tvName.setText(firstname);


                        }
                    }
                });
            }
        });

    }
}
