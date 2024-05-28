package com.rerbi007.gsheets;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class UniversalSender {
    static int timeOut;
    static RetryPolicy retryPolicy;
    public static void sendData(Context context, String urlData, Button sendData, ProgressBar progressBar) {
        timeOut = 50000;
        retryPolicy = new DefaultRetryPolicy(timeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        StringRequest stringRequest = new StringRequest(
                 Request.Method.GET,
                urlData,
                response -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    new AlertDialog.Builder(context)
                            .setTitle("Ответ сервера")
                            .setMessage(response)
                            .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            })
                            .show();
                    new Handler(Looper.getMainLooper()).postDelayed(() -> sendData.setEnabled(true), 100);
                },
                error -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    new AlertDialog.Builder(context)
                            .setTitle("Ошибка сохранения")
                            .setMessage(error.getMessage())
                            .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            })
                            .show();
                    new Handler(Looper.getMainLooper()).postDelayed(() -> sendData.setEnabled(true), 100);
                });
        stringRequest.setRetryPolicy(retryPolicy);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(stringRequest);
    }
}