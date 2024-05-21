package com.rerbi007.gsheets;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class UniversalSender {
    public static void sendData(Context context, String urlData  ) {
        StringRequest stringRequest = new StringRequest(
                 Request.Method.GET,
                urlData,
                response -> new AlertDialog.Builder(context)
                        .setTitle("Успех")
                        .setMessage(response)
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> { })
                        .show(),
                error -> new AlertDialog.Builder(context)
                        .setTitle("Ошибка сохранения")
                        .setMessage(error.getMessage())
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> { })
                        .show());
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(stringRequest);
    }
}