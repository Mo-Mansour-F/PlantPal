package com.mmf.plantpal.activities.useractivites;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.mmf.plantpal.R;
import com.mmf.plantpal.databinding.ActivityProfileBinding;
import com.mmf.plantpal.models.User;
import com.mmf.plantpal.utilteis.Constants;
import com.mmf.plantpal.utilteis.MsgAlert;
import com.mmf.plantpal.utilteis.MyFireBaseReferences;
import com.mmf.plantpal.utilteis.MySharedPreferencesManager;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ProfileActivity extends AppCompatActivity {
    ActivityProfileBinding binding;
    User user;


    boolean enable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        user = MySharedPreferencesManager.getUserLoginDetails(this);


        initView();
        initUserInfo();
    }


    private void initView() {
        binding.btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDelete();
            }
        });


        binding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (enable){
                    if (checkEmptyFields()) return;
                    updateUserInfo();
                }else {
                    enable = true;
                    enableField();
                    binding.btnUpdate.setText(getString(R.string.str_save));
                    binding.btnUpdate.setIcon(getDrawable(R.drawable.ic_success_check));
                }
            }
        });
    }

    private void initUserInfo() {
        binding.userEmail.setText(user.getEmail());
        binding.userName.setText(user.getName());
        binding.userAddress.setText(user.getAddress());
        binding.userPasswordEt.setText(user.getPassword());
        binding.userPasswordEt.setEnabled(false);

        enableField();

    }

    private void enableField(){
        binding.userName.setEnabled(enable);
        binding.userAddress.setEnabled(enable);
        binding.userPasswordEt.setEnabled(enable);
    }


    private void showConfirmDelete() {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(getString(R.string.str_confirm))
                .setContentText(getString(R.string.msg_confirm_delete_account))
                .setConfirmButton(getString(R.string.str_yes), new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        deleteAccount(sweetAlertDialog);
                    }
                })

                .setCancelButton(getString(R.string.str_no), new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                    }
                })
                .show();
    }


    private void deleteAccount(SweetAlertDialog sDialog) {
        sDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        sDialog.setTitleText(getString(R.string.str_please_wait));
        sDialog.setConfirmClickListener(null);
        sDialog.setCancelable(false);
        sDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);


        MyFireBaseReferences
                .getUsersReference()
                .child(user.getReferenceId())
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        MySharedPreferencesManager.clearUserData(ProfileActivity.this);
                        sDialog
                                .setTitleText("Deleted!")
                                .showCancelButton(false)
                                .setContentText(getString(R.string.msg_account_deleted))
                                .setConfirmText(getString(R.string.str_ok))
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        MySharedPreferencesManager.logout(ProfileActivity.this);
                                    }
                                })
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    }
                });
    }



    private void updateUserInfo(){
        MsgAlert.showProgress(this);
        String userName = binding.userName.getText().toString().trim();
        String address = binding.userAddress.getText().toString().trim();
        String password = binding.userPasswordEt.getText().toString().trim();

        user.setName(userName);
        user.setAddress(address);
        user.setPassword(password);

        MyFireBaseReferences
                .getUsersReference()
                .child(user.getReferenceId())
                .setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        MySharedPreferencesManager.saveUserLoginDetails(ProfileActivity.this, user);
                        MsgAlert.hideProgressDialog();
                        MsgAlert.showSuccessToast(ProfileActivity.this, getString(R.string.update_successfully));
                        finish();
                    }
                });
    }



    private boolean checkEmptyFields() {
        String userName = binding.userName.getText().toString().trim();
        String address = binding.userAddress.getText().toString().trim();
        String password = binding.userPasswordEt.getText().toString().trim();

        if (userName.length() == 0) {
            MsgAlert.showFieldError(getApplicationContext(), getString(R.string.error_enter_your_name), binding.userName);
            return true;
        }





        if (address.isEmpty()) {
            MsgAlert.showFieldError(getApplicationContext(), getString(R.string.error_enter_your_address),
                    binding.userAddress);
            return true;
        }

        if (password.isEmpty()) {
            MsgAlert.showFieldError(getApplicationContext(), getString(R.string.error_enter_your_password),
                    binding.userPasswordEt);
            return true;
        }

        return false;
    }
}