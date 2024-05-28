package com.rerbi007.gsheets;

import static android.text.TextUtils.isEmpty;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Collections;

public class StudyPlaceActivity extends AppCompatActivity {
    ProgressBar progressBar;
    TextView privacyPolicy;
    MyCaptcha captcha;
    String url;
    CheckBox isCorrect, iAgree, iAmNotRobot;
    EditText surname, name, patronymic, phone, email;
    Button sendData;
    Spinner courseSpinner, groupSpinner, copiesSpinner;
    ArrayList<String> groups = new ArrayList<>();
    ArrayList<String> filteredGroupNumbers = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_place);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_study_place), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        progressBar = findViewById(R.id.progress_bar);
        privacyPolicy = findViewById(R.id.privacy_policy);
        captcha = new MyCaptcha();
        url = getString(R.string.urlStudyPlace);
        surname = findViewById(R.id.surname);
        name = findViewById(R.id.name);
        patronymic = findViewById(R.id.patronymic);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        courseSpinner = findViewById(R.id.courseSpinner);
        groupSpinner = findViewById(R.id.groupSpinner);

        copiesSpinner = findViewById(R.id.copiesSpinner);
        isCorrect = findViewById(R.id.isCorrect);
        iAgree = findViewById(R.id.iAgree);
        iAmNotRobot = findViewById(R.id.iAmNotRobot);

        sendData = findViewById(R.id.saveData);
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
                String choose = ((String[]) getResources().getStringArray(R.array.course))[position];
                filteredGroupNumbers = new ArrayList<>();

                // Assuming groupNumbers is the original list of group numbers
                for (String groupNumber : groups) {
                    if (groupNumber.startsWith(choose)) {
                        filteredGroupNumbers.add(groupNumber);
                    }
                }

                if (filteredGroupNumbers.isEmpty()) {
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

        sendData.setOnClickListener(v -> {
            if (isEmpty(surname.getText().toString())) {
                Toast.makeText(StudyPlaceActivity.this, R.string.specifyLastName, Toast.LENGTH_SHORT).show();
                return;
            }
            if (isEmpty(name.getText().toString())) {
                Toast.makeText(StudyPlaceActivity.this, R.string.specifyName, Toast.LENGTH_SHORT).show();
                return;
            }
            if (isEmpty(patronymic.getText().toString()) && !((CheckBox) findViewById(R.id.isCorrect)).isChecked()) {
                Toast.makeText(StudyPlaceActivity.this, R.string.specifyPatronymic, Toast.LENGTH_SHORT).show();
                return;
            }
            if (isEmpty(phone.getText().toString())) {
                Toast.makeText(StudyPlaceActivity.this, R.string.specifyPhoneNumber, Toast.LENGTH_SHORT).show();
                return;
            }
            if (isEmpty(email.getText().toString())) {
                Toast.makeText(StudyPlaceActivity.this, R.string.specifyEmail, Toast.LENGTH_SHORT).show();
                return;
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()){
                Toast.makeText(StudyPlaceActivity.this, "Невалидный email", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!isCorrect.isChecked()) {
                Toast.makeText(StudyPlaceActivity.this, R.string.confirmDataCorrectness, Toast.LENGTH_SHORT).show();
                return;
            }
            if (!iAgree.isChecked()) {
                Toast.makeText(StudyPlaceActivity.this, R.string.acceptPersonalDataProcessing, Toast.LENGTH_SHORT).show();
                return;
            }
            if (!iAmNotRobot.isChecked()) {
                Toast.makeText(StudyPlaceActivity.this, R.string.iAmNotRobot, Toast.LENGTH_SHORT).show();
                return;
            }

            sendData.setEnabled(false);
            String request =
                    url
                            + "?action=create&surname=" + surname.getText().toString()
                            + "&name=" + name.getText().toString()
                            + "&patronymic=" + patronymic.getText().toString()
                            + "&phone=" + phone.getText().toString()
                            + "&email=" + email.getText().toString()
                            + "&group=" + groupSpinner.getSelectedItem().toString()
                            + "&numberOfCopies=" + copiesSpinner.getSelectedItem().toString();

            captcha.validate(StudyPlaceActivity.this, request, this, sendData, progressBar);
            //new Handler(Looper.getMainLooper()).postDelayed(() -> sendData.setEnabled(true), 10000); // Delay of 5 seconds (5000 milliseconds)
        });

        // переход по ссылке
        privacyPolicy.setMovementMethod(LinkMovementMethod.getInstance());
    }
}