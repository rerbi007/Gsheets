package com.rerbi007.gsheets;

import static android.text.TextUtils.isEmpty;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.recaptcha.Recaptcha;
import com.google.android.recaptcha.RecaptchaAction;
import com.google.android.recaptcha.RecaptchaTasksClient;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    @Nullable
    private RecaptchaTasksClient recaptchaTasksClient = null;
    CheckBox iAmNotRobot;
    String surname, name, patronymic, phoneNumber, groupNumber,  email, numberOfCopies;
    ArrayList<String> GroupNumbers = new ArrayList<>();
    ArrayList<String> filteredGroupNumbers = new ArrayList<>();
    ArrayAdapter<String> adapter;
    View saveData;

    Spinner courseSpinner, groupSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeRecaptchaClient();

        iAmNotRobot = findViewById(R.id.iAmNotRobot);
        saveData=findViewById(R.id.saveData);
        courseSpinner = findViewById(R.id.courseSpinner);
        // adding the initial elements
        Collections.addAll(GroupNumbers, getResources().getStringArray(R.array.groupNumbers));
        filteredGroupNumbers = new ArrayList<>(GroupNumbers);
        // getting the Spinner element
        groupSpinner = findViewById(R.id.groupSpinner);
        // creating an adapter
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, filteredGroupNumbers);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // installing an adapter for the list
        groupSpinner.setAdapter(adapter);

        courseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String choose = ((String[])getResources().getStringArray(R.array.course))[position];
                filteredGroupNumbers = new ArrayList<>();

                // Assuming groupNumbers is the original list of group numbers
                for (String groupNumber : GroupNumbers) {
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
        EditText[] editTextArray = {findViewById(R.id.surname), findViewById(R.id.name), findViewById(R.id.patronymic), findViewById(R.id.phone), findViewById(R.id.email)};

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
            surname = ((EditText)findViewById(R.id.surname)).getText().toString();
            name = ((EditText)findViewById(R.id.name)).getText().toString();
            patronymic = ((EditText)findViewById(R.id.patronymic)).getText().toString();
            phoneNumber = ((EditText)findViewById(R.id.phone)).getText().toString();
            groupNumber = groupSpinner.getSelectedItem().toString();
            email = ((EditText)findViewById(R.id.email)).getText().toString();
            numberOfCopies = ((Spinner)findViewById(R.id.numberOfCopies)).getSelectedItem().toString();

            if (isEmpty(surname)){
                Toast.makeText(MainActivity.this, R.string.specifyLastName, Toast.LENGTH_SHORT).show();
                return;
            }
            if (isEmpty(name)){
                Toast.makeText(MainActivity.this, R.string.specifyName, Toast.LENGTH_SHORT).show();
                return;
            }
            if (isEmpty(patronymic)&&!((CheckBox)findViewById(R.id.iConfirm)).isChecked()){
                Toast.makeText(MainActivity.this, R.string.specifyPatronymic, Toast.LENGTH_SHORT).show();
                return;
            }
            if(isEmpty(phoneNumber)){
                Toast.makeText(MainActivity.this, R.string.specifyPhoneNumber, Toast.LENGTH_SHORT).show();
                return;
            }
            if(isEmpty(email)){
                Toast.makeText(MainActivity.this, R.string.specifyEmail, Toast.LENGTH_SHORT).show();
                return;
            }

            if(!((CheckBox)findViewById(R.id.iConfirm)).isChecked()){
                Toast.makeText(MainActivity.this, R.string.confirmDataCorrectness, Toast.LENGTH_SHORT).show();
                return;
            }
            if(!((CheckBox)findViewById(R.id.iAgree)).isChecked()){
                Toast.makeText(MainActivity.this, R.string.acceptPersonalDataProcessing, Toast.LENGTH_SHORT).show();
                return;
            }
            if(!((CheckBox)findViewById(R.id.iAmNotRobot)).isChecked()){
                Toast.makeText(MainActivity.this, R.string.iAmNotRobot, Toast.LENGTH_SHORT).show();
                return;
            }

            saveData.setEnabled(false);
            sendData(surname, name, patronymic, phoneNumber, email, groupNumber, numberOfCopies);


            new Handler().postDelayed(() -> saveData.setEnabled(true), 5000); // Delay of 5 seconds (5000 milliseconds)
        });

        iAmNotRobot.setOnClickListener(this::executeLoginAction);
    }

    private void initializeRecaptchaClient() {
        Recaptcha
                .getTasksClient(getApplication(), "6Lf-wIYpAAAAAL2z5v35_p41OO-X-YxZZEL7b7hA")
                .addOnSuccessListener(
                        this,
                        new OnSuccessListener<RecaptchaTasksClient>() {
                            @Override
                            public void onSuccess(RecaptchaTasksClient client) {
                                MainActivity.this.recaptchaTasksClient = client;
                            }
                        })
                .addOnFailureListener(
                        this,
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle communication errors ...
                                // See "Handle communication errors" section
                            }
                        });
    }

    private void executeLoginAction(View v) {
        assert recaptchaTasksClient != null;
        recaptchaTasksClient
                .executeTask(RecaptchaAction.LOGIN)
                .addOnSuccessListener(
                        this,
                        new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(String token) {
                                // Handle success ...
                                // See "What's next" section for instructions
                                // about handling tokens.
                                //iAmNotRobot.setChecked(true);
                            }
                        })
                .addOnFailureListener(
                        this,
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle communication errors ...
                                // See "Handle communication errors" section
                                //iAmNotRobot.setChecked(false);
                            }
                        });
    }


    private void sendData(String surname, String name, String patronymic, String phoneNumber, String email, String groupNumber, String numberOfCopies) {
        String url="https://script.google.com/macros/s/AKfycby_Y6kU8NMu6CDEIoYdo52ykVvzp0yTCG-sIP0y0OstkmLZA6XiahGpymPp8LFvzjTq/exec?";
        url=url+"action=create&surname="+surname+"&name="+name+"&patronymic="+patronymic+"&phoneNumber="+phoneNumber+"&email="+email+"&groupNumber="+groupNumber+"&numberOfCopies="+numberOfCopies;

        StringRequest stringRequest=new StringRequest(Request.Method.GET, url, response -> Toast.makeText(MainActivity.this, response,Toast.LENGTH_SHORT).show(), error -> Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show());


        RequestQueue queue= Volley.newRequestQueue(this);
        queue.add(stringRequest);

    }
}