package com.mmf.plantpal.utilteis;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyFireBaseReferences {


    public static DatabaseReference getUsersReference(){
        return FirebaseDatabase.
                getInstance()
                .getReference()
                .child(Constants.KEY_FIRE_BASE_ROOT)
                .child(Constants.KEY_USERS);
    }



    public static DatabaseReference getPlantsReference(){
        return FirebaseDatabase.
                getInstance()
                .getReference()
                .child(Constants.KEY_FIRE_BASE_ROOT)
                .child(Constants.KEY_PLANTS);
    }



    public static DatabaseReference getAccessoriesReference(){
        return FirebaseDatabase.
                getInstance()
                .getReference()
                .child(Constants.KEY_FIRE_BASE_ROOT)
                .child(Constants.KEY_ACCESSORIES);
    }




    public static DatabaseReference getFavoriteReference(){
        return FirebaseDatabase.
                getInstance()
                .getReference()
                .child(Constants.KEY_FIRE_BASE_ROOT)
                .child(Constants.KEY_FAVORITES);
    }


    public static DatabaseReference getContactMessageReference(){
        return FirebaseDatabase.
                getInstance()
                .getReference()
                .child(Constants.KEY_FIRE_BASE_ROOT)
                .child(Constants.KEY_CONTACT_MESSAGE);
    }
}
