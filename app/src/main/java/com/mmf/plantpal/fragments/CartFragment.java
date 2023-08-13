package com.mmf.plantpal.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.mmf.plantpal.R;
import com.mmf.plantpal.adapters.CartItemAdapter;
import com.mmf.plantpal.databinding.FragmentAccessoryBinding;
import com.mmf.plantpal.databinding.FragmentCartBinding;
import com.mmf.plantpal.listerners.OnCartItemChangedListener;
import com.mmf.plantpal.models.Item;
import com.mmf.plantpal.repository.DataRepository;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class CartFragment extends Fragment {
    FragmentCartBinding binding;
    private OnCartItemChangedListener onCartItemChangedListener;


    CartItemAdapter cartItemAdapter;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof OnCartItemChangedListener) {
            onCartItemChangedListener = (OnCartItemChangedListener) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentCartBinding.inflate(inflater);

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();
        initRecyclerView();
    }

    @Override
    public void onResume() {
        super.onResume();

        setItemList();
    }

    private void initView() {
        binding.btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkoutInvoice();
            }
        });
    }

    private void initRecyclerView() {
        cartItemAdapter = new CartItemAdapter(requireContext(), binding);

        cartItemAdapter.setOnItemCartClickListener(new CartItemAdapter.OnItemCartClickListener() {
            @Override
            public void onButtonRemoveClick(Item item) {
                DataRepository.getItemList().remove(item.getReferenceId());
                onCartItemChangedListener.onOnCartItemChanged();
                setItemList();
            }
        });

        binding.cartItemRecyclerView.setHasFixedSize(true);
        binding.cartItemRecyclerView.setAdapter(cartItemAdapter);
    }


    private void checkoutInvoice() {
        new SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("Won't be able to recover this file!")
                .setConfirmText("Yes,delete it!")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
//                        sDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
//                        sDialog.setTitleText("Loading");
//                        sDialog.setConfirmClickListener(null);
//                        sDialog.setCancelable(false);
//                        sDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);

                        sswait(sDialog);
                    }
                })
                .show();

    }


    private void sswait(SweetAlertDialog sDialog) {
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        sDialog
                .setTitleText("Deleted!")
                .setContentText("Your imaginary file has been deleted!")
                .setConfirmText("OK")
                .setConfirmClickListener(null)
                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);


        DataRepository.getItemList().clear();
        onCartItemChangedListener.onOnCartItemChanged();
        setItemList();
    }


    private void setItemList() {
        cartItemAdapter.setItemList(DataRepository.getItemsCart());
    }


    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.findItem(R.id.menu_sort).setVisible(false);

        super.onPrepareOptionsMenu(menu);
    }
}