package com.mmf.plantpal.activities.adminactivites;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mmf.plantpal.R;
import com.mmf.plantpal.databinding.ActivityAddAccessoryBinding;
import com.mmf.plantpal.models.Accessory;
import com.mmf.plantpal.models.Plant;
import com.mmf.plantpal.repository.DataRepository;
import com.mmf.plantpal.utilteis.Constants;
import com.mmf.plantpal.utilteis.MsgAlert;
import com.mmf.plantpal.utilteis.MyFireBaseReferences;

import java.util.Locale;

public class AddAccessoryActivity extends AppCompatActivity {
    ActivityAddAccessoryBinding binding;

    ActivityResultLauncher<Intent> activityResultLauncher_get_image_from_studio;

    Accessory accessoryUpdate;

    Uri accessoryImageUri = null;


    ArrayAdapter<String> typesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddAccessoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Retrieve accessory data for updating or null if adding a new accessory
        accessoryUpdate = (Accessory) getIntent().getSerializableExtra(Constants.KEY_ACCESSORY_PARAM);


        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Set the title and button text based on whether it's an update or add operation
        if (accessoryUpdate == null) {
            setTitle(getString(R.string.str_add_accessory));
            binding.btnSaveAccessory.setText(getString(R.string.str_save));
        } else {
            setTitle(getString(R.string.str_update_accessory));
            binding.btnSaveAccessory.setText(getString(R.string.str_update));
            // Fill the form with existing accessory data for updating
            fillAccessoryData();
        }

        // Initialize the type dropdown list
        initTypes();
        initView();
        initLauncher();
    }


    // Initialize the type dropdown list
    private void initTypes() {
        typesAdapter = new ArrayAdapter<>(AddAccessoryActivity.this, android.R.layout.simple_list_item_1, DataRepository.getTypesList());
        binding.accessoryType.setAdapter(typesAdapter);
    }


    private void initView() {
        binding.btnSaveAccessory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkEmptyFields()) return;

                if (accessoryUpdate == null){
                    uploadImage(); // upload the image and Add a new accessory
                }else {
                    updateAccessory(); // Update an existing accessory
                }
            }
        });



        // Handle the accessory image selection from the gallery
        binding.accessoryCoverLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentGetPhotoFromStudio = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                if (intentGetPhotoFromStudio.resolveActivity(getPackageManager()) != null) {
                    activityResultLauncher_get_image_from_studio.launch(intentGetPhotoFromStudio);
                } else {
                    Toast.makeText(AddAccessoryActivity.this, getString(R.string.error_no_app), Toast.LENGTH_SHORT).show();
                }
            }
        });


        binding.btnDeleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearImageDate();
            }
        });
    }

    // Handle uploading the selected accessory image to Firebase Storage
    private void uploadImage() {
        MsgAlert.showProgressWithTitle(this, getString(R.string.str_uploading_image));
        String image_name = "" + System.currentTimeMillis() + "." + GetFileExtension(accessoryImageUri);

        StorageReference ref = FirebaseStorage
                .getInstance()
                .getReference()
                .child(Constants.ACCESSORY_IMAGE_FOLDER + "/" + image_name);


        ref.putFile(accessoryImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isComplete()) ;
                        Uri uri = uriTask.getResult();
                        saveAccessory(uri.toString());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error, Image not uploaded
                        MsgAlert.hideProgressDialog();
                        MsgAlert.showErrorDialog(getString(R.string.str_error_not_expected), AddAccessoryActivity.this);
                    }
                })
                .addOnProgressListener(this, new OnProgressListener<UploadTask.TaskSnapshot>() {
                    // Progress Listener for loading
                    // percentage on the dialog box
                    @Override
                    public void onProgress(
                            @NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());

                        String loadingProgress = String.format(Locale.US, "%s %d %%",
                                getString(R.string.str_status_uploading_image),
                                (int) progress);

                        MsgAlert.changeProgressMessage(loadingProgress);
                    }
                });


    }

    // Save accessory data to Firebase
    private void saveAccessory(String imagePath) {
        DatabaseReference reference = MyFireBaseReferences
                .getAccessoriesReference()
                .push();

        String accessoryName = binding.accessoryName.getText().toString().trim();
        String accessoryType = binding.accessoryType.getText().toString().trim();
        String accessoryPrice = binding.accessoryPrice.getText().toString().trim();
        String accessoryStock = binding.accessoryStock.getText().toString().trim();
        String usageInstructions = binding.usageInstruction.getText().toString().trim();
        String descriptions = binding.description.getText().toString().trim();


        float price = Float.parseFloat(accessoryPrice);
        int stock = Integer.parseInt(accessoryStock);

        Accessory accessory = new Accessory();
        accessory.setReferenceId(reference.getKey());
        accessory.setName(accessoryName);
        accessory.setType(accessoryType);
        accessory.setPrice(price);
        accessory.setStock(stock);
        accessory.setUsageInstructions(usageInstructions);
        accessory.setDescription(descriptions);
        accessory.setImagePath(imagePath);


        reference
                .setValue(accessory)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        MsgAlert.hideProgressDialog();
                        MsgAlert.showSuccessToast(AddAccessoryActivity.this, getString(R.string.add_successfully));
                        clearFields(); // clear filed to add a new accessory
                        DataRepository.addType(accessoryType); // add the new type to the old type list
                        initTypes(); // get all type including the new one that has been added
                    }
                });


    }

    // Update an existing accessory in Firebase
    private void updateAccessory(){
        MsgAlert.showProgress(this);

        String accessoryName = binding.accessoryName.getText().toString().trim();
        String accessoryType = binding.accessoryType.getText().toString().trim();
        String accessoryPrice = binding.accessoryPrice.getText().toString().trim();
        String accessoryStock = binding.accessoryStock.getText().toString().trim();
        String usageInstructions = binding.usageInstruction.getText().toString().trim();
        String description = binding.description.getText().toString().trim();

        float price = Float.parseFloat(accessoryPrice);
        int stock = Integer.parseInt(accessoryStock);

        accessoryUpdate.setName(accessoryName);
        accessoryUpdate.setType(accessoryType);
        accessoryUpdate.setPrice(price);
        accessoryUpdate.setStock(stock);
        accessoryUpdate.setDescription(description);
        accessoryUpdate.setUsageInstructions(usageInstructions);




        MyFireBaseReferences
                .getAccessoriesReference()
                .child(accessoryUpdate.getReferenceId())
                .setValue(accessoryUpdate)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        MsgAlert.hideProgressDialog();
                        MsgAlert.showSuccessToast(AddAccessoryActivity.this, getString(R.string.update_successfully));
                        finish();
                    }
                });
    }

    // Clear all input fields
    private void clearFields() {
        binding.accessoryName.getText().clear();
        binding.accessoryType.getText().clear();
        binding.accessoryPrice.getText().clear();
        binding.accessoryStock.getText().clear();
        binding.usageInstruction.getText().clear();
        binding.description.getText().clear();
        clearImageDate();
    }

    // Clear the selected accessory image
    private void clearImageDate() {
        binding.accessoryImage.setImageResource(R.drawable.plantimage);
        binding.accessoryImageBlur.setImageResource(0);
        accessoryImageUri = null;
        binding.btnDeleteImage.setVisibility(View.GONE);
    }



    private boolean checkEmptyFields() {
        String accessoryName = binding.accessoryName.getText().toString().trim();
        String accessoryType = binding.accessoryType.getText().toString().trim();
        String accessoryPrice = binding.accessoryPrice.getText().toString().trim();
        String accessoryStock = binding.accessoryStock.getText().toString().trim();
        String usageInstructions = binding.usageInstruction.getText().toString().trim();
        String descriptions = binding.description.getText().toString().trim();

        if (accessoryName.length() == 0) {
            MsgAlert.showFieldError(getApplicationContext(), getString(R.string.error_required), binding.accessoryName);
            return true;
        }

        if (accessoryType.length() == 0) {
            MsgAlert.showFieldError(getApplicationContext(), getString(R.string.error_required), binding.accessoryType);
            return true;
        }

        if (accessoryPrice.length() == 0) {
            MsgAlert.showFieldError(getApplicationContext(), getString(R.string.error_required), binding.accessoryPrice);
            return true;
        }

        if (accessoryStock.length() == 0) {
            MsgAlert.showFieldError(getApplicationContext(), getString(R.string.error_required), binding.accessoryStock);
            return true;
        }


        float price = Float.parseFloat(accessoryPrice);


        if (price == 0) {
            MsgAlert.showFieldError(getApplicationContext(), getString(R.string.error_required), binding.accessoryPrice);
            return true;
        }


        int stock = Integer.parseInt(accessoryStock);


        if (stock == 0) {
            MsgAlert.showFieldError(getApplicationContext(), getString(R.string.error_required), binding.accessoryStock);
            return true;
        }



        if (usageInstructions.isEmpty()) {
            MsgAlert.showFieldError(getApplicationContext(), getString(R.string.error_required), binding.usageInstruction);
            return true;
        }


        if (descriptions.isEmpty()) {
            MsgAlert.showFieldError(getApplicationContext(), getString(R.string.error_required), binding.description);
            return true;
        }




        if (accessoryImageUri == null) {
            if (accessoryUpdate == null){
                MsgAlert.showErrorToast(getApplicationContext(), getString(R.string.error_image_required));
                return true;
            }
        }

        return false;
    }

    // Fill the form with existing accessory data for updating
    private void fillAccessoryData() {
        binding.accessoryName.setText(accessoryUpdate.getName());
        binding.accessoryType.setText(accessoryUpdate.getType());
        binding.accessoryPrice.setText(String.valueOf(accessoryUpdate.getPrice()));
        binding.accessoryStock.setText(String.valueOf(accessoryUpdate.getStock()));
        binding.usageInstruction.setText(accessoryUpdate.getUsageInstructions());
        binding.description.setText(accessoryUpdate.getDescription());



        Glide.with(this)
                .load(accessoryUpdate.getImagePath())
                .error(R.drawable.plantimage)
                .placeholder(R.drawable.plantimage)
                .into(binding.accessoryImage);



        Glide.with(this)
                .load(accessoryUpdate.getImagePath())
                .error(R.drawable.plantimage)
                .placeholder(R.drawable.plantimage)
                .into(binding.accessoryImageBlur);
    }



    // Get the file extension of the selected accessory image
    private String GetFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    // Initialize the activity result launcher for selecting an image from the gallery
    private void initLauncher() {
        activityResultLauncher_get_image_from_studio = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult()
                , new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            if (result.getData().getData() != null) {
                                accessoryImageUri = result.getData().getData();
                                binding.accessoryImage.setImageURI(result.getData().getData());
                                binding.accessoryImageBlur.setImageURI(result.getData().getData());
                                binding.btnDeleteImage.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
        );

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}