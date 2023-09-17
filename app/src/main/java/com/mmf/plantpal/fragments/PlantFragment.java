package com.mmf.plantpal.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mmf.plantpal.R;
import com.mmf.plantpal.activities.adminactivites.AddPlantActivity;
import com.mmf.plantpal.activities.useractivites.ShowPlantActivity;
import com.mmf.plantpal.adapters.PlantAdapter;
import com.mmf.plantpal.databinding.FragmentPlantBinding;
import com.mmf.plantpal.listerners.OnCartItemChangedListener;
import com.mmf.plantpal.models.CartItem;
import com.mmf.plantpal.models.Plant;
import com.mmf.plantpal.models.User;
import com.mmf.plantpal.repository.DataRepository;
import com.mmf.plantpal.utilteis.Constants;
import com.mmf.plantpal.utilteis.MsgAlert;
import com.mmf.plantpal.utilteis.MyFireBaseReferences;
import com.mmf.plantpal.utilteis.MySharedPreferencesManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class PlantFragment extends Fragment {

    FragmentPlantBinding binding;

    private OnCartItemChangedListener onCartItemChangedListener;

    PlantAdapter plantAdapter;
    List<Plant> plantList = new ArrayList<>();
    List<Plant> searchedPlantsList = new ArrayList<>();
    List<Plant> filteredPlantsList = new ArrayList<>();


    List<String> selectedSpecies = new ArrayList<>();
    boolean[] checkedSpecies;
    int sortBy = 0;
    String searchValue = "";


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof OnCartItemChangedListener){
            onCartItemChangedListener = (OnCartItemChangedListener) context;
        }
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // to make the fragment listening for the option menu item clicked
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        binding = FragmentPlantBinding.inflate(inflater);

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();
        initRecyclerView();
    }



    @Override
    public void onStart() {
        super.onStart();
        getPlants();
    }

    private void initView() {
        User user = MySharedPreferencesManager.getUserLoginDetails(requireContext());
        // show or hide the add plant btn based on the role of the user
        if (user.getRole() == Constants.ROLE_ADMIN) {
            binding.btnAddPlant.setVisibility(View.VISIBLE);
        } else {
            binding.btnAddPlant.setVisibility(View.GONE);
        }


        binding.searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchValue = newText;
                searchList();
                return false;
            }
        });


        binding.btnFilterList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterList();
            }
        });


        binding.btnAddPlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireContext(), AddPlantActivity.class));
            }
        });


    }

    private void initRecyclerView() {
        plantAdapter = new PlantAdapter(requireContext());


        plantAdapter.setOnPlantClickListener(new PlantAdapter.OnPlantClickListener() {
            @Override
            public void onClick(View view, Plant plant) {
                Intent intent = new Intent(requireContext(), ShowPlantActivity.class);
                intent.putExtra(Constants.KEY_PLANTS_PARAM, plant);


                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                        view, ViewCompat.getTransitionName(view));
                startActivity(intent, options.toBundle());
            }

            @Override
            public void onDeleteButtonClick(Plant plant) {
                showConfirmDelete(plant);
            }

            @Override
            public void onUpdateButtonClick(Plant plant) {
                Intent intent = new Intent(requireContext(), AddPlantActivity.class);
                intent.putExtra(Constants.KEY_PLANTS_PARAM, plant);
                startActivity(intent);
            }

            @Override
            public void onAddToCartButtonClick(Plant plant) {
                CartItem cartItem = new CartItem();
                cartItem.setName(plant.getName());
                cartItem.setPrice(plant.getPrice());
                cartItem.setStock(plant.getStock());
                cartItem.setImagePath(plant.getImagePath());
                cartItem.setReferenceId(plant.getReferenceId());

                DataRepository.addItemCart(cartItem);

                onCartItemChangedListener.onOnCartItemChanged();

            }
        });


        binding.plantRecyclerView.setHasFixedSize(true);
        binding.plantRecyclerView.setAdapter(plantAdapter);

    }


    private void getPlants() {
        MyFireBaseReferences
                .getPlantsReference()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        plantList.clear();

                        for (DataSnapshot child : snapshot.getChildren()) {
                            Plant plant = child.getValue(Plant.class);
                            plantList.add(plant);
                            if (plant.getSpecies() == null) continue;

                            DataRepository.addSpecies(plant.getSpecies());
                        }

                        if (checkedSpecies == null){
                            checkedSpecies = new boolean[DataRepository.getSpeciesList().size()];
                        }

                        searchList();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private void searchList() {
        // delete old search data
        searchedPlantsList.clear();
        filteredPlantsList.clear();

        // 1 filter
        // if there is species filter apply
        if (selectedSpecies.size() > 0) {
            // to get only the plant with selected species
            filteredPlantsList = plantList.stream()
                    .filter(plant -> selectedSpecies.contains(plant.getSpecies()))
                    .collect(Collectors.toList());
        }
        else {
            // if no species filter apply get all plant
            filteredPlantsList.addAll(plantList);
        }

        // 2 search
        // if search text is empty
        if (searchValue.trim().isEmpty()) {
            searchedPlantsList.addAll(filteredPlantsList);
        } else {
            // if search text have value then search for the text and ignore the case
            searchedPlantsList = filteredPlantsList.stream()
                    .filter(plant -> plant.getName().toLowerCase().contains(searchValue.toLowerCase()))
                    .collect(Collectors.toList());
        }


        // 3 sort
        switch (sortBy) {
            case 0:
            default:
                //sort by name in accesnding order
                searchedPlantsList = searchedPlantsList.stream()
                        .sorted(new Comparator<Plant>() {
                            @Override
                            public int compare(Plant o1, Plant o2) {
                                return (o1.getName().toLowerCase()).compareTo(o2.getName().toLowerCase());
                            }
                        })
                        .collect(Collectors.toList());
                break;


            case 1:
                // sort by price
                searchedPlantsList.sort(new Comparator<Plant>() {
                    @Override
                    public int compare(Plant o1, Plant o2) {
                        return Float.compare(o1.getPrice(), o2.getPrice());
                    }
                });


                break;


            case 2:
                //sort by species
                searchedPlantsList.sort(new Comparator<Plant>() {
                    @Override
                    public int compare(Plant o1, Plant o2) {
                        return (o1.getSpecies()).compareTo(o2.getSpecies());
                    }
                });
                break;


        }


        plantAdapter.setPlantList(searchedPlantsList);

        if (searchedPlantsList.size() == 0) {
            if (searchValue.trim().isEmpty()) {
                binding.state.setText(getString(R.string.str_no_plant));
            } else {
                binding.state.setText(getString(R.string.str_no_result));
            }
            binding.progressHorizontal.setVisibility(View.GONE);
            binding.state.setVisibility(View.VISIBLE);
            binding.progressHorizontalLayout.setVisibility(View.VISIBLE);
        } else {
            binding.progressHorizontalLayout.setVisibility(View.GONE);
        }

    }

    private void showSortList() {
        AlertDialog alertDialog;
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setTitle(getString(R.string.str_sort_by));


        builder.setSingleChoiceItems(getResources().getStringArray(R.array.sort_by_for_plants), sortBy, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sortBy = which;
            }
        });

        builder.setPositiveButton(getString(R.string.str_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                searchList();
            }
        });


        builder.setNegativeButton(getString(R.string.str_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog = builder.create();
        alertDialog.show();

    }

    private void showFilterList() {
        String[] speciesArray = DataRepository.getSpeciesList().toArray(new String[DataRepository.getSpeciesList().size()]);

        AlertDialog alertDialog;
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setTitle(getString(R.string.str_filter_by));

        builder.setMultiChoiceItems(speciesArray, checkedSpecies, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                checkedSpecies[which] = isChecked;
            }
        });

        builder.setPositiveButton(getString(R.string.str_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Process the selected items
                selectedSpecies.clear();
                for (int i = 0; i < checkedSpecies.length; i++) {
                    if (checkedSpecies[i]) {
                        selectedSpecies.add(speciesArray[i]);
                    }
                }


                searchList();
            }
        });

        builder.setNegativeButton(getString(R.string.str_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle cancel button click
            }
        });


        alertDialog = builder.create();
        alertDialog.show();
    }


    private void showConfirmDelete(Plant plant) {

        String dialogMsg = String.format(Locale.US, "%s '%s'",
                getString(R.string.msg_confirm_delete),
                plant.getName());

        SweetAlertDialog errorDialog = new SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE);
        errorDialog.setTitleText(getString(R.string.str_confirm));
        errorDialog.setContentText(dialogMsg);
        errorDialog.setConfirmButton(getString(R.string.str_yes), new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                errorDialog.dismissWithAnimation();
                deletePlant(plant);
            }
        });

        errorDialog.setCancelButton(getString(R.string.str_cancel), new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                errorDialog.dismissWithAnimation();
            }
        });
        errorDialog.show();
    }

    private void deletePlant(Plant plant) {
        MsgAlert.showProgress(requireContext());
        MyFireBaseReferences
                .getPlantsReference()
                .child(plant.getReferenceId())
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        MsgAlert.hideProgressDialog();
                        MsgAlert.showSuccessToast(requireContext(), getString(R.string.delete_successfully));
                    }
                });
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.findItem(R.id.menu_sort).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showSortList();
                return false;
            }
        });

        super.onPrepareOptionsMenu(menu);
    }


}