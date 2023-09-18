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
import com.mmf.plantpal.databinding.ActivityAddPlantBinding;
import com.mmf.plantpal.models.Plant;
import com.mmf.plantpal.repository.DataRepository;
import com.mmf.plantpal.utilteis.Constants;
import com.mmf.plantpal.utilteis.MsgAlert;
import com.mmf.plantpal.utilteis.MyFireBaseReferences;

import java.util.Locale;

public class AddPlantActivity extends AppCompatActivity {
    ActivityAddPlantBinding binding;

    ArrayAdapter<String> speciesAdapter;

    ActivityResultLauncher<Intent> activityResultLauncher_get_image_from_studio;

    Uri plantImageUri = null;


    Plant plantUpdate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddPlantBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Retrieve plant data for updating or null if adding a new plant
        plantUpdate = (Plant) getIntent().getSerializableExtra(Constants.KEY_PLANTS_PARAM);


        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Set the title and button text based on whether it's an update or add operation
        if (plantUpdate == null) {
            setTitle(getString(R.string.str_add_plant));
            binding.btnSavePlant.setText(getString(R.string.str_save));
        } else {
            setTitle(getString(R.string.title_update_plant));
            binding.btnSavePlant.setText(getString(R.string.str_update));

            // Fill the form with existing plant data for updating
            fillPlantData();
        }

        // Initialize the species dropdown list
        initSpecies();
        initView();
        initLauncher();
    }

    // Initialize the species dropdown list
    private void initSpecies() {
        speciesAdapter = new ArrayAdapter<>(AddPlantActivity.this, android.R.layout.simple_list_item_1, DataRepository.getSpeciesList());
        binding.plantSpecies.setAdapter(speciesAdapter);
    }


    private void initView() {
        binding.btnSavePlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkEmptyFields()) return;
                if (plantUpdate == null){
                    uploadImage(); // upload the image and Add a new plant
                }else {
                    updatePlant(); // Update an existing plant
                }
            }
        });


        // Handle the plant image selection from the gallery
        binding.plantCoverLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentGetPhotoFromStudio = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                if (intentGetPhotoFromStudio.resolveActivity(getPackageManager()) != null) {
                    activityResultLauncher_get_image_from_studio.launch(intentGetPhotoFromStudio);
                } else {
                    Toast.makeText(AddPlantActivity.this, getString(R.string.error_no_app), Toast.LENGTH_SHORT).show();
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


    // Handle uploading the selected plant image to Firebase Storage
    private void uploadImage() {
        MsgAlert.showProgressWithTitle(this, getString(R.string.str_uploading_image));
        String image_name = "" + System.currentTimeMillis() + "." + GetFileExtension(plantImageUri);

        StorageReference ref = FirebaseStorage
                .getInstance()
                .getReference()
                .child(Constants.PLANTS_IMAGE_FOLDER + "/" + image_name);


        ref.putFile(plantImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isComplete()) ;
                        Uri uri = uriTask.getResult();
                        savePlant(uri.toString());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error, Image not uploaded
                        MsgAlert.hideProgressDialog();
                        MsgAlert.showErrorDialog(getString(R.string.str_error_not_expected), AddPlantActivity.this);
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


    // Save plant data to Firebase
    private void savePlant(String imagePath) {
        String plantName = binding.plantName.getText().toString().trim();
        String plantSpecies = binding.plantSpecies.getText().toString().trim();
        String plantPrice = binding.plantPrice.getText().toString().trim();
        String plantStock = binding.plantStock.getText().toString().trim();
        String care_instruction = binding.careInstructions.getText().toString().trim();
        String growHabit = binding.growHabit.getText().toString().trim();
        String description = binding.description.getText().toString().trim();

        float price = Float.parseFloat(plantPrice);
        int stock = Integer.parseInt(plantStock);

        DatabaseReference reference = MyFireBaseReferences
                .getPlantsReference()
                .push();

        Plant plant = new Plant();
        plant.setName(plantName);
        plant.setSpecies(plantSpecies);
        plant.setPrice(price);
        plant.setStock(stock);
        plant.setReferenceId(reference.getKey());
        plant.setImagePath(imagePath);
        plant.setDescription(description);
        plant.setCareInstructions(care_instruction);
        plant.setGrowHabit(growHabit);


        reference
                .setValue(plant)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        MsgAlert.hideProgressDialog();
                        MsgAlert.showSuccessToast(AddPlantActivity.this, getString(R.string.add_successfully));
                        clearFields(); // clear filed to add a new plant
                        DataRepository.addSpecies(plantSpecies); // add the new species to the old species list
                        initSpecies(); // get all species including the new one that has been added
                    }
                });


    }

    // Update an existing plant in Firebase
    private void updatePlant(){
        MsgAlert.showProgress(this);

        String plantName = binding.plantName.getText().toString().trim();
        String plantSpecies = binding.plantSpecies.getText().toString().trim();
        String plantPrice = binding.plantPrice.getText().toString().trim();
        String plantStock = binding.plantStock.getText().toString().trim();
        String care_instruction = binding.careInstructions.getText().toString().trim();
        String growHabit = binding.growHabit.getText().toString().trim();
        String description = binding.description.getText().toString().trim();

        float price = Float.parseFloat(plantPrice);
        int stock = Integer.parseInt(plantStock);

        plantUpdate.setName(plantName);
        plantUpdate.setSpecies(plantSpecies);
        plantUpdate.setPrice(price);
        plantUpdate.setStock(stock);
        plantUpdate.setDescription(description);
        plantUpdate.setCareInstructions(care_instruction);
        plantUpdate.setGrowHabit(growHabit);


        MyFireBaseReferences
                .getPlantsReference()
                .child(plantUpdate.getReferenceId())
                .setValue(plantUpdate)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        MsgAlert.hideProgressDialog();
                        MsgAlert.showSuccessToast(AddPlantActivity.this, getString(R.string.update_successfully));
                        finish();
                    }
                });
    }


    // Get the file extension of the selected plant image
    private String GetFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    // Clear all input fields
    private void clearFields() {
        binding.plantName.getText().clear();
        binding.plantPrice.getText().clear();
        binding.plantStock.getText().clear();
        binding.plantSpecies.getText().clear();
        binding.careInstructions.getText().clear();
        binding.growHabit.getText().clear();
        binding.description.getText().clear();
        clearImageDate();

    }


    // Clear the selected plant image
    private void clearImageDate() {
        binding.plantImage.setImageResource(R.drawable.plantimage);
        binding.plantImageBlur.setImageResource(0);
        plantImageUri = null;
        binding.btnDeleteImage.setVisibility(View.GONE);
    }

    private boolean checkEmptyFields() {
        String plantName = binding.plantName.getText().toString().trim();
        String plantSpecies = binding.plantSpecies.getText().toString().trim();
        String plantPrice = binding.plantPrice.getText().toString().trim();
        String plantStock = binding.plantStock.getText().toString().trim();
        String care_instruction = binding.careInstructions.getText().toString().trim();
        String growHabit = binding.growHabit.getText().toString().trim();
        String description = binding.description.getText().toString().trim();

        if (plantName.length() == 0) {
            MsgAlert.showFieldError(getApplicationContext(), getString(R.string.error_required), binding.plantName);
            return true;
        }

        if (plantSpecies.length() == 0) {
            MsgAlert.showFieldError(getApplicationContext(), getString(R.string.error_required), binding.plantSpecies);
            return true;
        }
        if (plantPrice.length() == 0) {
            MsgAlert.showFieldError(getApplicationContext(), getString(R.string.error_required), binding.plantPrice);
            return true;
        }

        if (plantStock.length() == 0) {
            MsgAlert.showFieldError(getApplicationContext(), getString(R.string.error_required), binding.plantStock);
            return true;
        }


        float price = Float.parseFloat(plantPrice);


        if (price == 0) {
            MsgAlert.showFieldError(getApplicationContext(), getString(R.string.error_required), binding.plantPrice);
            return true;
        }


        int stock = Integer.parseInt(plantStock);


        if (stock == 0) {
            MsgAlert.showFieldError(getApplicationContext(), getString(R.string.error_required), binding.plantStock);
            return true;
        }


        if (care_instruction.isEmpty()) {
            MsgAlert.showFieldError(getApplicationContext(), getString(R.string.error_required), binding.careInstructions);
            return true;
        }

        if (growHabit.isEmpty()) {
            MsgAlert.showFieldError(getApplicationContext(), getString(R.string.error_required), binding.growHabit);
            return true;
        }

        if (description.isEmpty()) {
            MsgAlert.showFieldError(getApplicationContext(), getString(R.string.error_required), binding.description);
            return true;
        }


        if (plantImageUri == null) {
            if (plantUpdate == null){
                MsgAlert.showErrorToast(getApplicationContext(), getString(R.string.error_image_required));
                return true;
            }
        }

        return false;
    }


    // Fill the form with existing plant data for updating
    private void fillPlantData() {
        binding.plantName.setText(plantUpdate.getName());
        binding.plantSpecies.setText(plantUpdate.getSpecies());
        binding.plantPrice.setText(String.valueOf(plantUpdate.getPrice()));
        binding.plantStock.setText(String.valueOf(plantUpdate.getStock()));
        binding.careInstructions.setText(plantUpdate.getCareInstructions());
        binding.growHabit.setText(plantUpdate.getGrowHabit());
        binding.description.setText(plantUpdate.getDescription());



        Glide.with(this)
                .load(plantUpdate.getImagePath())
                .error(R.drawable.plantimage)
                .placeholder(R.drawable.plantimage)
                .into(binding.plantImage);



        Glide.with(this)
                .load(plantUpdate.getImagePath())
                .error(R.drawable.plantimage)
                .placeholder(R.drawable.plantimage)
                .into(binding.plantImageBlur);
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
                                plantImageUri = result.getData().getData();
                                binding.plantImage.setImageURI(result.getData().getData());
                                binding.plantImageBlur.setImageURI(result.getData().getData());
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