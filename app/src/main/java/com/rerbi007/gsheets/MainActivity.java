package com.rerbi007.gsheets;

import static android.text.TextUtils.isEmpty;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    CheckBox isCorrect, iAgree, iAmNotRobot;
    EditText surname, name, patronymic, phone,  email;
    View saveData;
    Spinner courseSpinner, groupSpinner, copiesSpinner;
    ArrayList<String> groups = new ArrayList<>();
    ArrayList<String> filteredGroupNumbers = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        surname = findViewById(R.id.surname);
        name = findViewById(R.id.name);
        patronymic = findViewById(R.id.patronymic);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email); //((EditText)findViewById(R.id.email)).getText().toString();
        courseSpinner = findViewById(R.id.courseSpinner);
        groupSpinner = findViewById(R.id.groupSpinner);

        copiesSpinner = findViewById(R.id.copiesSpinner); //((Spinner)findViewById(R.id.copiesSpinner)).getSelectedItem().toString();

        iAmNotRobot = findViewById(R.id.iAmNotRobot);
        saveData=findViewById(R.id.saveData);
        // adding the initial elements
        Collections.addAll(groups, getResources().getStringArray(R.array.groupNumbers));
        filteredGroupNumbers = new ArrayList<>(groups);
        // getting the Spinner element
        groupSpinner = findViewById(R.id.groupSpinner);
        // creating an adapter
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, filteredGroupNumbers);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // installing an adapter for the list
        groupSpinner.setAdapter(adapter);

        // filter groupNumbers by courseSpinner
        courseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String choose = ((String[])getResources().getStringArray(R.array.course))[position];
                filteredGroupNumbers = new ArrayList<>();

                // Assuming groupNumbers is the original list of group numbers
                for (String groupNumber : groups) {
                    if (groupNumber.startsWith(choose)) {
                        filteredGroupNumbers.add(groupNumber);
                    }
                }

                if(filteredGroupNumbers.isEmpty()) {
                    Log.d("Spinner", "Filtered list is empty");
                } else {
                    // Clear the existing data in the adapter and add the filtered list
                    adapter.clear();
                    adapter.addAll(filteredGroupNumbers);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Array to store the 5 EditText views
        EditText[] editTextArray = {surname, name, patronymic, phone, email};

        for (int i = 0; i < editTextArray.length; i++) {
            final EditText currentEditText = editTextArray[i];
            final EditText nextEditText = (i < editTextArray.length - 1) ? editTextArray[i + 1] : null;

            currentEditText.setOnKeyListener((v, keyCode, event) -> {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if (nextEditText != null) {
                        nextEditText.requestFocus();
                        return true;
                    }
                }
                return false;
            });
        }

        findViewById(R.id.email).setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                findViewById(R.id.courseSpinner).requestFocus();

                return true;
            }
            return false;
        });

        saveData.setOnClickListener(v -> {
            if (isEmpty(surname.getText().toString())){
                Toast.makeText(MainActivity.this, R.string.specifyLastName, Toast.LENGTH_SHORT).show();
                return;
            }
            if (isEmpty(name.getText().toString())){
                Toast.makeText(MainActivity.this, R.string.specifyName, Toast.LENGTH_SHORT).show();
                return;
            }
            if (isEmpty(patronymic.getText().toString())&&!((CheckBox)findViewById(R.id.isCorrect)).isChecked()){
                Toast.makeText(MainActivity.this, R.string.specifyPatronymic, Toast.LENGTH_SHORT).show();
                return;
            }
            if(isEmpty(phone.getText().toString())){
                Toast.makeText(MainActivity.this, R.string.specifyPhoneNumber, Toast.LENGTH_SHORT).show();
                return;
            }
            if(isEmpty(email.getText().toString())){
                Toast.makeText(MainActivity.this, R.string.specifyEmail, Toast.LENGTH_SHORT).show();
                return;
            }
            if(!isCorrect.isChecked()){
                Toast.makeText(MainActivity.this, R.string.confirmDataCorrectness, Toast.LENGTH_SHORT).show();
                return;
            }
            if(!iAgree.isChecked()){
                Toast.makeText(MainActivity.this, R.string.acceptPersonalDataProcessing, Toast.LENGTH_SHORT).show();
                return;
            }
            if(!iAmNotRobot.isChecked()){
                Toast.makeText(MainActivity.this, R.string.iAmNotRobot, Toast.LENGTH_SHORT).show();
                return;
            }

            saveData.setEnabled(false);
            sendData(
                    surname.getText().toString(),
                    name.getText().toString(),
                    patronymic.getText().toString(),
                    phone.getText().toString(),
                    email.getText().toString(),
                    groupSpinner.getSelectedItem().toString(),
                    copiesSpinner.getSelectedItem().toString()
            );
            new Handler(Looper.getMainLooper()).postDelayed(() -> saveData.setEnabled(true), 5000); // Delay of 5 seconds (5000 milliseconds)
        });
    }



    private void sendData(String surname, String name, String patronymic, String phone, String email, String groupNumber, String numberOfCopies) {
        String url="https://script.google.com/macros/s/AKfycby_Y6kU8NMu6CDEIoYdo52ykVvzp0yTCG-sIP0y0OstkmLZA6XiahGpymPp8LFvzjTq/exec?";
        url=url+"action=create&surname="+surname+"&name="+name+"&patronymic="+patronymic+"&phone="+phone+"&email="+email+"&groupNumber="+groupNumber+"&numberOfCopies="+numberOfCopies;

        StringRequest stringRequest=new StringRequest(Request.Method.GET, url, response -> Toast.makeText(MainActivity.this, response,Toast.LENGTH_SHORT).show(), error -> Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show());


        RequestQueue queue= Volley.newRequestQueue(this);
        queue.add(stringRequest);

    }
}