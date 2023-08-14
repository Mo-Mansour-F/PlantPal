package com.mmf.plantpal.activities.loginactivites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.util.Patterns;
import android.view.View;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.mmf.plantpal.R;
import com.mmf.plantpal.activities.useractivites.UserMainActivity;
import com.mmf.plantpal.databinding.ActivitySignUpBinding;
import com.mmf.plantpal.models.User;
import com.mmf.plantpal.appapi.RetrofitClient;
import com.mmf.plantpal.utilteis.Constants;
import com.mmf.plantpal.utilteis.MsgAlert;
import com.mmf.plantpal.utilteis.MyFireBaseReferences;
import com.mmf.plantpal.utilteis.MySharedPreferencesManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;

import okhttp3.ResponseBody;


public class SignUpActivity extends AppCompatActivity {
    ActivitySignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        initView();
    }


    private void initView() {
        String text = getString(R.string.str_already_have_account);
        SpannableString ss = new SpannableString(text);
        UnderlineSpan underlineSpan = new UnderlineSpan();
        ss.setSpan(underlineSpan, 25, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.btnLogin.setText(ss);


        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkEmptyFieldsAndValidate()) return;
                checkIfEmailExist();
            }
        });
    }


    private void checkIfEmailExist() {
        MsgAlert.showProgress(this);
        String email = binding.userEmailEt.getText().toString().trim();


        MyFireBaseReferences
                .getUsersReference()
                .orderByChild("email")
                .equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NotNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            MsgAlert.hideProgressDialog();
                            MsgAlert.showErrorDialog(getString(R.string.str_email_exist), SignUpActivity.this);
                        } else {
                            createNewAccount();
                        }
                    }

                    @Override
                    public void onCancelled(@NotNull DatabaseError error) {

                    }
                });
    }

    private void createNewAccount() {
        String userName = binding.userNameEt.getText().toString().trim();
        String email = binding.userEmailEt.getText().toString().trim();
        String address = binding.userAddressEt.getText().toString().trim();
        String password = binding.userPasswordEt.getText().toString().trim();


        DatabaseReference reference = MyFireBaseReferences
                .getUsersReference()
                .push();

        User user = new User();
        user.setReferenceId(reference.getKey());
        user.setName(userName);
        user.setEmail(email);
        user.setAddress(address);
        user.setPassword(password);
        user.setRole(Constants.ROLE_USER);


        reference
                .setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        sendWelcomeEmail(user);

                    }
                });
    }


    private boolean checkEmptyFieldsAndValidate() {
        String userName = binding.userNameEt.getText().toString().trim();
        String email = binding.userEmailEt.getText().toString().trim();
        String address = binding.userAddressEt.getText().toString().trim();
        String password = binding.userPasswordEt.getText().toString().trim();

        if (userName.length() == 0) {
            MsgAlert.showFieldError(getApplicationContext(), getString(R.string.error_enter_your_name), binding.userNameEt);
            return true;
        }

        if (email.length() == 0) {
            MsgAlert.showFieldError(getApplicationContext(), getString(R.string.error_enter_your_email),
                    binding.userEmailEt);
            return true;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            MsgAlert.showFieldError(getApplicationContext(), getString(R.string.error_email_not_validation),
                    binding.userEmailEt);
            return true;
        }


        if (!isValidPassword(password)){
            MsgAlert.showFieldError(getApplicationContext(), getString(R.string.error_password_not_validation),
                    binding.userEmailEt);
            return true;
        }



        if (address.isEmpty()) {
            MsgAlert.showFieldError(getApplicationContext(), getString(R.string.error_enter_your_address),
                    binding.userAddressEt);
            return true;
        }


        if (password.isEmpty()) {
            MsgAlert.showFieldError(getApplicationContext(), getString(R.string.error_enter_your_password),
                    binding.userPasswordEt);
            return true;
        }

        return false;
    }


    public void sendWelcomeEmail(User user) {

        String from = getString(R.string.str_contact_mail);
        String subject = "Welcome Message";


        String helloMessage = "Hello" +
                " " +
                user.getName() +
                " " +
                "In PlantPal App," +
                " " +
                "Your Register Successfully," +
                "Thank You.";



        RetrofitClient.getInstance()
                .getApi()
                .sendEmail(from, user.getEmail(), subject, helloMessage)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull retrofit2.Response<ResponseBody> response) {
                        login(user);
//                        if (response.code() == HTTP_OK) {
//                            try {
//                                JSONObject obj = new JSONObject(response.body().string());
//                                Toast.makeText(SignUpActivity.this, obj.getString("message"), Toast.LENGTH_LONG).show();
//                            } catch (JSONException | IOException e) {
//                                MsgAlert.showErrorDialog(e.getMessage(), SignUpActivity.this);
//
//                            }
//                        }else {
//                            login(user);
//                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                        login(user);
                    }
                });
    }



    private void login(User user){
        MsgAlert.hideProgressDialog();
        MySharedPreferencesManager.saveUserLoginDetails(SignUpActivity.this, user);
        Intent intent = new Intent(SignUpActivity.this, UserMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }



    public static boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }
}