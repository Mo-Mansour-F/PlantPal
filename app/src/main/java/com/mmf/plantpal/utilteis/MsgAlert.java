package com.mmf.plantpal.utilteis;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;

import com.google.android.material.textfield.TextInputEditText;
import com.mmf.plantpal.R;
import com.mmf.plantpal.databinding.CustomErrorToastLayoutBinding;
import com.mmf.plantpal.databinding.CustomSuccessToastLayoutBinding;


import cn.pedant.SweetAlert.SweetAlertDialog;

public class MsgAlert {
    private static ProgressDialog progressDialog;


    public static void showAlertDialog(String dialogMsg, Context context) {
        SweetAlertDialog errorDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
        errorDialog.setTitleText(context.getString(R.string.str_alert));
        errorDialog.setContentText(dialogMsg);
        errorDialog.setConfirmText(context.getString(R.string.str_ok));
        errorDialog.show();

    }

    public static void showErrorDialog(String msg, Activity context) {
        SweetAlertDialog errorDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
        errorDialog.setTitleText(context.getString(R.string.str_error));
        errorDialog.setContentText(msg);
        errorDialog.setConfirmText(context.getString(R.string.str_ok));
        errorDialog.show();
    }


    public static void showSuccessDialog(String msg, Activity context) {
        SweetAlertDialog errorDialog = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE);
        errorDialog.setTitleText(context.getString(R.string.str_success));
        errorDialog.setContentText(msg);
        errorDialog.setConfirmText(context.getString(R.string.str_ok));
        errorDialog.show();

    }

    public static void showSuccessToast(Context context, String msg){
        CustomSuccessToastLayoutBinding customSuccessToastLayoutBinding =
                CustomSuccessToastLayoutBinding.bind(LayoutInflater.from(context).inflate(R.layout.custom_success_toast_layout, null));
        customSuccessToastLayoutBinding.message.setText(msg);

        showToast(context, customSuccessToastLayoutBinding.getRoot());
    }


    public static void showErrorToast(Context context, String msg){
        CustomErrorToastLayoutBinding customErrorToastLayoutBinding =
                CustomErrorToastLayoutBinding.bind(LayoutInflater.from(context).inflate(R.layout.custom_error_toast_layout, null));
        customErrorToastLayoutBinding.message.setText(msg);

        showToast(context, customErrorToastLayoutBinding.getRoot());
    }

    private static void showToast(Context context, View view){
        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view);
        toast.show();
    }


    public static void showProgress(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(context.getString(R.string.str_please_wait));
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

    public static void showProgressWithTitle(Context context, String message) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(context.getString(R.string.str_please_wait));
        progressDialog.setMessage(message);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

    public static void changeProgressMessage(String message) {
        progressDialog.setMessage(message);
    }

    public static void hideProgressDialog() {
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog.cancel();
            }
        }
    }



    public static void showFieldError(Context context, String errorMessage, TextInputEditText inputEditText) {
        inputEditText.setError(errorMessage);
        inputEditText.requestFocus();
        showErrorToast(context, errorMessage);
    }

    public static void showFieldError(Context context, String errorMessage, AppCompatAutoCompleteTextView autoCompleteTextView) {
        autoCompleteTextView.setError(errorMessage);
        autoCompleteTextView.requestFocus();
        showErrorToast(context, errorMessage);
    }



}