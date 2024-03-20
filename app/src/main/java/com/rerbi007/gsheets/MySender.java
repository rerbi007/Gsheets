package com.rerbi007.gsheets;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class MySender {
    public static void sendData(Context context, String url, String surname, String name, String patronymic, String phone, String email, String groupNumber, String numberOfCopies) {
        url += "action=create&surname="+surname+"&name="+name+"&patronymic="+patronymic+"&phone="+phone+"&email="+email+"&groupNumber="+groupNumber+"&numberOfCopies="+numberOfCopies;
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                response -> Toast.makeText(context, response, Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show());
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(stringRequest);
    }
}
