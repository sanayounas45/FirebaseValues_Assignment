package com.example.firebasevalues;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore objectFirebaseFirestore;
    private CollectionReference ObjectCollectionReference;
    private DocumentReference objectDocumentReference;
    private Dialog objectDialog;
    private EditText STIDET,STNAMEET,STROLLET;
    private TextView documentDataTV;
    private static final String Collection_Name="BCS6A";
    private static final String Student_Name="name";
    private static final String Student_Roll="rollno";
    private String completeData ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        objectDialog=new Dialog(this);
        objectDialog.setContentView(R.layout.please_wait);

        STIDET=findViewById(R.id.STIDET);
        STNAMEET=findViewById(R.id.STNAMEET);
        STROLLET=findViewById(R.id.STROLLET);

        documentDataTV = findViewById(R.id.documentDataTV);
        documentDataTV.setMovementMethod(new ScrollingMovementMethod());
        try {
            objectFirebaseFirestore=FirebaseFirestore.getInstance();
            ObjectCollectionReference=objectFirebaseFirestore.collection(Collection_Name);
        }
        catch (Exception e) {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    public void addDataToFirebase(View v) {

        try {
            documentDataTV.setText("");
            completeData ="";
            objectFirebaseFirestore = FirebaseFirestore.getInstance();
            objectFirebaseFirestore.collection(Collection_Name).document(STIDET.getText().toString()).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (task.getResult().exists()) {
                                Toast.makeText(MainActivity.this, "Document ID Already Exist", Toast.LENGTH_SHORT).show();
                            }

                            else
                            {
                                if(!STIDET.getText().toString().isEmpty() && !STNAMEET.getText().toString().isEmpty() && !STROLLET.getText().toString().isEmpty()) {
                                    objectDialog.show();
                                    Map<String,Object> objMap=new HashMap<>();
                                    objMap.put(Student_Name, STNAMEET.getText().toString());
                                    objMap.put(Student_Roll, STROLLET.getText().toString());
                                    objectFirebaseFirestore.collection(Collection_Name)
                                            .document(STIDET.getText().toString()).set(objMap)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    objectDialog.dismiss();
                                                    STIDET.setText("");
                                                    STNAMEET.setText("");
                                                    STROLLET.setText("");
                                                    STIDET.requestFocus();
                                                    Toast.makeText(MainActivity.this, "Document Added Successfully", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    objectDialog.dismiss();
                                                    Toast.makeText(MainActivity.this, "Fails To Add Data", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                                else
                                {
                                    Toast.makeText(MainActivity.this, "Please Enter Valid Details", Toast.LENGTH_SHORT).show();
                                }
                            }

                        }
                    });
        }
        catch (Exception e)
        {
            Toast.makeText(this, "Add Values"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    public void  showAllDataFromFirebase(View v)
    {

        try
        {
            completeData ="";
            objectDialog.show();
            documentDataTV.setText("");
            ObjectCollectionReference.get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            objectDialog.dismiss();

                            for (DocumentSnapshot objectDocumentReference : queryDocumentSnapshots) {
                                String EMPID = objectDocumentReference.getId();
                                String STName = objectDocumentReference.getString(Student_Name);
                                String STRoll = objectDocumentReference.getString(Student_Roll);
                                completeData += "Student ID : " + EMPID + '\n'
                                        + "Student Name : " + STName + '\n'
                                        + "Student Roll No: " + STRoll +'\n'
                                        +"___________________________________"+'\n' ;
                            }
                            documentDataTV.setText(completeData);
                            Toast.makeText(MainActivity.this,"Retrieve Data Successfully",Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    objectDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Fails to retrieve data:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
        catch(Exception e)
        {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    public void  deleteDocumentFromFirebase(View v)
    {
        try
        {
            if(STIDET.getText().toString().isEmpty())
            {
                Toast.makeText(this, "Please Enter the Document ID", Toast.LENGTH_SHORT).show();
            }
            else {
                if (!STIDET.getText().toString().isEmpty()) {
                    objectDocumentReference = objectFirebaseFirestore.collection(Collection_Name)
                            .document(STIDET.getText().toString());
                    objectDocumentReference.delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    objectDialog.dismiss();
                                    STIDET.setText("");
                                    Toast.makeText(MainActivity.this, "Document Deleted Successfully", Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    objectDialog.dismiss();
                                    Toast.makeText(MainActivity.this, "Fails To Delete", Toast.LENGTH_LONG).show();
                                }
                            });
                } else {
                    Toast.makeText(this, "Fails to Delete The Document", Toast.LENGTH_LONG);
                }
            }
        }
        catch (Exception e)
        {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }


    public void  collectionDeletion(String ID)
    {
        try
        {
            objectDocumentReference = objectFirebaseFirestore.collection(Collection_Name)
                    .document(ID);
            objectDocumentReference.delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            objectDialog.dismiss();
                            Toast.makeText(MainActivity.this,"Collection Deleted Successfully",Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            objectDialog.dismiss();
                            Toast.makeText(MainActivity.this,"Fails to Delete",Toast.LENGTH_LONG).show();
                        }
                    });
        }
        catch (Exception e)
        {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
    public void deleteCollectionFromFirebase(View v)
    {

        objectDialog.show();
        ObjectCollectionReference.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        objectDialog.dismiss();
                        for (DocumentSnapshot objectDocumentReference : queryDocumentSnapshots) {
                            String std_ID = objectDocumentReference.getId();
                            collectionDeletion(std_ID);
                        }
                        Toast.makeText(MainActivity.this,"Collection Delete Successfully",Toast.LENGTH_LONG).show();
                    }

                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                objectDialog.dismiss();
                Toast.makeText(MainActivity.this, "Fails to Delete:" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
