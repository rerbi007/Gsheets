package com.rerbi007.gsheets;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ContactsActivity extends AppCompatActivity {
    TextView vk, telegram, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contacts);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        vk = findViewById(R.id.findMeVk);
        telegram = findViewById(R.id.findMeTelegram);
        email = findViewById(R.id.findMeEmail);

        vk.setMovementMethod(LinkMovementMethod.getInstance());
        telegram.setMovementMethod(LinkMovementMethod.getInstance());
        email.setMovementMethod(LinkMovementMethod.getInstance());
    }
}