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
            public void onButtonRemoveClick(String itemReference) {
                DataRepository.getItemList().remove(itemReference);
                onCartItemChangedListener.onOnCartItemChanged();
                setItemList();
            }
        });

        binding.cartItemRecyclerView.setHasFixedSize(true);
        binding.cartItemRecyclerView.setAdapter(cartItemAdapter);
    }


    private void checkoutInvoice() {
        new SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText(getString(R.string.str_confirm))
                .setContentText("Are you sure to checkout items?")
                .setConfirmButton(getString(R.string.str_yes), new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                        DataRepository.getItemList().clear();
                        onCartItemChangedListener.onOnCartItemChanged();
                        setItemList();


                        sweetAlertDialog
                                .showCancelButton(false)
                                .setTitleText(getString(R.string.str_success))
                                .setContentText("Checkout Success")
                                .setConfirmText(getString(R.string.str_ok))
                                .setConfirmClickListener(null)
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    }
                })
                .setCancelButton(getString(R.string.str_cancel), new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                    }
                })
                .show();

    }


    private void setItemList() {
        cartItemAdapter.setCartItemList(DataRepository.getItemsCart());
    }


    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.findItem(R.id.menu_sort).setVisible(false);

        super.onPrepareOptionsMenu(menu);
    }
}