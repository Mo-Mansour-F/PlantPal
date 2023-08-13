package com.mmf.plantpal.utilteis;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.mmf.plantpal.activities.loginactivites.LoginActivity;
import com.mmf.plantpal.models.User;


public class MySharedPreferencesManager {

    public static void saveStringValue(Context context, String key, String value){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
        editor.commit();
    }

    public static void saveIntValue(Context context, String key, int value){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
        editor.commit();
    }

    public static String getStringValue(Context context, String key){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, null);
    }

    public static int getIntValue(Context context, String key){
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(key, 0);
    }


    public static void saveUserLoginDetails(Context context, User user){
        saveStringValue(context, Constants.KEY_REFERENCE_ID, user.getReferenceId());
        saveStringValue(context, Constants.KEY_NAME, user.getName());
        saveStringValue(context, Constants.KEY_EMAIL, user.getEmail());
        saveStringValue(context, Constants.KEY_PASSWORD, user.getPassword());
        saveStringValue(context, Constants.KEY_ADDRESS, user.getAddress());
        saveIntValue(context, Constants.KEY_ROLE, user.getRole());
    }


    public static User getUserLoginDetails(Context context){
        User user = new User();
        user.setReferenceId(getStringValue(context, Constants.KEY_REFERENCE_ID));
        user.setName(getStringValue(context, Constants.KEY_NAME));
        user.setEmail(getStringValue(context, Constants.KEY_EMAIL));
        user.setPassword(getStringValue(context, Constants.KEY_PASSWORD));
        user.setAddress(getStringValue(context, Constants.KEY_ADDRESS));
        user.setRole(getIntValue(context, Constants.KEY_ROLE));
        return user;
    }




    public static void logout(Activity context){

        clearUserData(context);

        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        context.finish();
    }

    public static void clearUserData(Activity context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        editor.commit();
    }
}
