package com.mmf.plantpal.activities.loginactivites;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.util.Patterns;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.mmf.plantpal.R;
import com.mmf.plantpal.activities.adminactivites.AdminMainActivity;
import com.mmf.plantpal.activities.useractivites.UserMainActivity;
import com.mmf.plantpal.databinding.ActivityLoginBinding;
import com.mmf.plantpal.models.User;
import com.mmf.plantpal.utilteis.Constants;
import com.mmf.plantpal.utilteis.MsgAlert;
import com.mmf.plantpal.utilteis.MyFireBaseReferences;
import com.mmf.plantpal.utilteis.MySharedPreferencesManager;


public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        initView();


    }


    @Override
    protected void onStart() {
        super.onStart();
        checkIsLogin();
    }

    private void initView() {
        String text = getString(R.string.str_not_have_account);
        SpannableString ss = new SpannableString(text);
        UnderlineSpan underlineSpan = new UnderlineSpan();
        ss.setSpan(underlineSpan, 21, 27, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.btnSignUp.setText(ss);


        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkEmptyFields()) return;
                login();
            }
        });


        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });
    }


    private void login() {
        MsgAlert.showProgress(this);

        String email = binding.emailEt.getText().toString().trim();
        String password = binding.passwordEt.getText().toString().trim();


        MyFireBaseReferences
                .getUsersReference()
                .orderByChild("email")
                .equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NotNull DataSnapshot snapshot) {
                        MsgAlert.hideProgressDialog();
                        if (snapshot.exists()) {
                            for (DataSnapshot child : snapshot.getChildren()) {
                                User user = child.getValue(User.class);
                                if (user.getPassword().equals(password)) {
                                    MySharedPreferencesManager.saveUserLoginDetails(LoginActivity.this, user);
                                    goToMainActivity(user);
                                    break;
                                } else {
                                    MsgAlert.showErrorDialog(getString(R.string.str_password_not_correct), LoginActivity.this);
                                }
                            }
                        } else {
                            MsgAlert.showErrorDialog(getString(R.string.str_not_exist_this_email), LoginActivity.this);
                        }
                    }

                    @Override
                    public void onCancelled(@NotNull DatabaseError error) {

                    }
                });

    }

    private boolean checkEmptyFields() {
        String email = binding.emailEt.getText().toString().trim();
        String password = binding.passwordEt.getText().toString().trim();

        if (email.length() == 0) {
            MsgAlert.showFieldError(getApplicationContext(), getString(R.string.error_enter_your_email), binding.emailEt);
            return true;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            MsgAlert.showFieldError(getApplicationContext(), getString(R.string.error_email_not_validation), binding.emailEt);
            return true;
        }


        if (password.length() == 0) {
            MsgAlert.showFieldError(getApplicationContext(), getString(R.string.error_enter_your_password), binding.passwordEt);
            return true;
        }

        return false;
    }


    private void checkIsLogin() {
        User user = MySharedPreferencesManager.getUserLoginDetails(this);
        if (user.getEmail() != null) {
            goToMainActivity(user);
        }
    }


    private void goToMainActivity(User user) {
        Intent intent;
        if (user.getRole() == Constants.ROLE_ADMIN) {
            intent = new Intent(LoginActivity.this, AdminMainActivity.class);
        } else {
            intent = new Intent(LoginActivity.this, UserMainActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}