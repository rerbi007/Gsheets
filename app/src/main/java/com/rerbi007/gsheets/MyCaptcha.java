package com.rerbi007.gsheets;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.safetynet.SafetyNet;

public class MyCaptcha {

    public void validate(Activity activity, String request, Context context, Button sendData, ProgressBar progressBar) {
        SafetyNet.getClient(activity).verifyWithRecaptcha(activity.getString(R.string.siteKey))
                .addOnSuccessListener(recaptchaTokenResponse -> {
                    // Indicates communication with reCAPTCHA service was
                    // successful.
                    String userResponseToken = recaptchaTokenResponse.getTokenResult();
                    assert userResponseToken != null;
                    if (!userResponseToken.isEmpty()) {
                        // Validate the user response token using the
                        // reCAPTCHA site verify API.
                        Log.d(TAG, String.format("user response token is %s", userResponseToken));
                        sendData.setEnabled(false);
                        progressBar.setVisibility(View.VISIBLE);
                        UniversalSender.sendData(context, request+"&userResponseToken="+userResponseToken, sendData, progressBar);
                    }
                })
                .addOnFailureListener(e -> {
                    if (e instanceof ApiException) {
                        // An error occurred when communicating with the
                        // reCAPTCHA service. Refer to the status code to
                        // handle the error appropriately.
                        ApiException apiException = (ApiException) e;
                        int statusCode = apiException.getStatusCode();
                        Log.d(TAG, "Error: " + CommonStatusCodes
                                .getStatusCodeString(statusCode));
                        new AlertDialog.Builder(context)
                                .setTitle("Ошибка ReCaptcha")
                                .setMessage("Error: " + CommonStatusCodes
                                        .getStatusCodeString(statusCode))
                                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                })
                                .show();
                    } else {
                        // A different, unknown type of error occurred.
                        Log.d(TAG, "Error: " + e.getMessage());
                        new AlertDialog.Builder(context)
                                .setTitle("Ошибка ReCaptcha")
                                .setMessage("Error: " + e.getMessage())
                                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                })
                                .show();
                    }
                });
    }
}
