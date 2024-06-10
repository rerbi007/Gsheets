package com.rerbi007.gsheets;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainFormActivity extends AppCompatActivity {

    Button studyPlace, Scholarship, Invocation, Contacts;
    Intent myIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_form);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_main_form), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        studyPlace = findViewById(R.id.studyPlace);
        studyPlace.setOnClickListener(v -> {
            myIntent = new Intent(this, StudyPlaceActivity.class);
            startActivity(myIntent);
        });

        Scholarship = findViewById(R.id.scholarship);
        Scholarship.setOnClickListener(v -> {
            myIntent = new Intent(this, ScholarshipActivity.class);
            startActivity(myIntent);
        });

        Invocation = findViewById(R.id.invocation);
        Invocation.setOnClickListener(v -> {
            myIntent = new Intent(this, InvocationActivity.class);
            startActivity(myIntent);
        });

        Contacts = findViewById(R.id.contacts);
        Contacts.setOnClickListener(v -> {
            myIntent = new Intent(this, ContactsActivity.class);
            startActivity(myIntent);
        });
    }
}