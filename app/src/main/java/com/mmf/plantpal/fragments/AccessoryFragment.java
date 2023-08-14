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
import com.mmf.plantpal.activities.adminactivites.AddAccessoryActivity;
import com.mmf.plantpal.activities.useractivites.ShowAccessoryActivity;
import com.mmf.plantpal.adapters.AccessoryAdapter;
import com.mmf.plantpal.databinding.FragmentAccessoryBinding;
import com.mmf.plantpal.listerners.OnCartItemChangedListener;
import com.mmf.plantpal.models.Accessory;
import com.mmf.plantpal.models.CartItem;
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


public class AccessoryFragment extends Fragment {
    FragmentAccessoryBinding binding;
    private OnCartItemChangedListener onCartItemChangedListener;

    AccessoryAdapter accessoryAdapter;
    List<Accessory> accessoryList = new ArrayList<>();

    List<Accessory> searchedAccessoryList = new ArrayList<>();
    List<Accessory> filteredAccessoryList = new ArrayList<>();


    List<String> selectedTypes = new ArrayList<>();
    boolean[] checkedTypes;
    int sortBy = 0;
    String searchValue = "" ;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof OnCartItemChangedListener){
            onCartItemChangedListener = (OnCartItemChangedListener) context;
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initRecyclerView();


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentAccessoryBinding.inflate(inflater);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        getAccessories();
    }

    private void initView(){
        User user = MySharedPreferencesManager.getUserLoginDetails(requireContext());
        if (user.getRole() == Constants.ROLE_ADMIN){
            binding.btnAddAccessory.setVisibility(View.VISIBLE);
        }else {
            binding.btnAddAccessory.setVisibility(View.GONE);
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


        binding.btnAddAccessory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireContext(), AddAccessoryActivity.class));
            }
        });
    }


    private void initRecyclerView() {
        accessoryAdapter = new AccessoryAdapter(requireContext());
        accessoryAdapter.setOnAccessoryClickListener(new AccessoryAdapter.OnAccessoryClickListener() {
            @Override
            public void onClick(View view, Accessory accessory) {
                Intent intent = new Intent(requireContext(), ShowAccessoryActivity.class);
                intent.putExtra(Constants.KEY_ACCESSORY_PARAM, accessory);




                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                        view, ViewCompat.getTransitionName(view));
                startActivity(intent, options.toBundle());
            }

            @Override
            public void onDeleteButtonClick(Accessory accessory) {
                showConfirmDelete(accessory);
            }

            @Override
            public void onUpdateButtonClick(Accessory accessory) {
                Intent intent = new Intent(requireContext(), AddAccessoryActivity.class);
                intent.putExtra(Constants.KEY_ACCESSORY_PARAM, accessory);
                startActivity(intent);
            }

            @Override
            public void onAddToCartButtonClick(Accessory accessory) {
                CartItem cartItem = new CartItem();
                cartItem.setName(accessory.getName());
                cartItem.setPrice(accessory.getPrice());
                cartItem.setStock(accessory.getStock());
                cartItem.setImagePath(accessory.getImagePath());
                cartItem.setReferenceId(accessory.getReferenceId());

                DataRepository.addItemCart(cartItem);

//                DataRepository.getItemList().put(plant.getReferenceId(), cartItem);
                onCartItemChangedListener.onOnCartItemChanged();
            }
        });


        binding.accessoryRecyclerView.setHasFixedSize(true);
        binding.accessoryRecyclerView.setAdapter(accessoryAdapter);


    }


    private void getAccessories(){
        MyFireBaseReferences
                .getAccessoriesReference()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        accessoryList.clear();

                        for (DataSnapshot child : snapshot.getChildren()) {
                            Accessory accessory = child.getValue(Accessory.class);
                            accessoryList.add(accessory);

                            if (accessory.getType() == null) continue;

                            DataRepository.addType(accessory.getType());
                        }

                        checkedTypes = new boolean[DataRepository.getTypesList().size()];

                        searchList();



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private void searchList() {
        searchedAccessoryList.clear();
        filteredAccessoryList.clear();

        // 1 filter
        if (selectedTypes.size() > 0) {
            filteredAccessoryList = accessoryList.stream()
                    .filter(accessory -> selectedTypes.contains(accessory.getType()))
                    .collect(Collectors.toList());
        } else {
            filteredAccessoryList.addAll(accessoryList);
        }

        // 2 search
        if (searchValue.trim().isEmpty()) {
            searchedAccessoryList.addAll(filteredAccessoryList);
        } else {
            searchedAccessoryList = filteredAccessoryList.stream()
                    .filter(accessory -> accessory.getName().toLowerCase().contains(searchValue.toLowerCase()))
                    .collect(Collectors.toList());
        }


        // 3 sort


        switch (sortBy) {
            case 0:
            default:
                //sort by name
                searchedAccessoryList = searchedAccessoryList.stream()
                        .sorted(new Comparator<Accessory>() {
                            @Override
                            public int compare(Accessory accessory1, Accessory accessory2) {
                                return (accessory1.getName()).compareTo(accessory2.getName());
                            }
                        })
                        .collect(Collectors.toList());
                break;


            case 1:
                // sort by price
                searchedAccessoryList.sort(new Comparator<Accessory>() {
                    @Override
                    public int compare(Accessory accessory1, Accessory accessory2) {
                        return Float.compare(accessory1.getPrice(), accessory2.getPrice());
                    }
                });


                break;


            case 2:
                //sort by species


                searchedAccessoryList.sort(new Comparator<Accessory>() {
                    @Override
                    public int compare(Accessory accessory1, Accessory accessory2) {
                        return (accessory1.getType().toLowerCase()).compareTo(accessory2.getType().toLowerCase());
                    }
                });
                break;


        }


        accessoryAdapter.setAccessoriesList(searchedAccessoryList);

        if (searchedAccessoryList.size() == 0) {
            if (searchValue.trim().isEmpty()) {
                binding.state.setText(getString(R.string.str_no_accessory));
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


        builder.setSingleChoiceItems(getResources().getStringArray(R.array.sort_by_for_accessory), sortBy, new DialogInterface.OnClickListener() {
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


//
//
//        String[] items = {"name", "Price", "species"};
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireContext(), android.R.layout.select_dialog_singlechoice, items) {
//            @NonNull
//            @Override
//            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//                View view = super.getView(position, convertView, parent);
//                AppCompatCheckedTextView radioButton = view.findViewById(android.R.id.text1);
//                radioButton.setChecked(position == sortBy);
//                return view;
//            }
//        };
//
//
//        Holder holder = new ViewHolder(R.layout.select_dialog_list);
//        DialogPlus dialog = DialogPlus.newDialog(requireContext())
//                .setContentHolder(holder)
//                .setGravity(Gravity.CENTER)
//                .setCancelable(true)
//                .setExpanded(false)
//                .create();
//
//
//        ListView listView = (ListView) dialog.getHolderView();
//        listView.setAdapter(adapter);
//
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                sortBy = position;
//                adapter.notifyDataSetChanged();
//                dialog.dismiss();
//                searchList();
//            }
//        });
//
//
//        dialog.show();
    }

    private void showFilterList() {
        String[] typesArray = DataRepository.getTypesList().toArray(new String[DataRepository.getTypesList().size()]);

        AlertDialog alertDialog;
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setTitle(getString(R.string.str_filter_by));

        builder.setMultiChoiceItems(typesArray, checkedTypes, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                checkedTypes[which] = isChecked;
            }
        });

        builder.setPositiveButton(getString(R.string.str_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Process the selected items
                selectedTypes.clear();
                for (int i = 0; i < checkedTypes.length; i++) {
                    if (checkedTypes[i]) {
                        selectedTypes.add(typesArray[i]);
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




    private void showConfirmDelete(Accessory accessory) {

        String dialogMsg = String.format(Locale.US, "%s '%s'",
                getString(R.string.msg_confirm_delete),
                accessory.getName());

        SweetAlertDialog errorDialog = new SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE);
        errorDialog.setTitleText(getString(R.string.str_confirm));
        errorDialog.setContentText(dialogMsg);
        errorDialog.setConfirmButton(getString(R.string.str_yes), new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                errorDialog.dismissWithAnimation();
                deleteAccessory(accessory);
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

    private void deleteAccessory(Accessory accessory) {
        MsgAlert.showProgress(requireContext());
        MyFireBaseReferences
                .getAccessoriesReference()
                .child(accessory.getReferenceId())
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