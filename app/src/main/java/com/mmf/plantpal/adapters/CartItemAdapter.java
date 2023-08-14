package com.mmf.plantpal.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mmf.plantpal.R;
import com.mmf.plantpal.databinding.CartItemLayoutBinding;
import com.mmf.plantpal.databinding.FragmentCartBinding;
import com.mmf.plantpal.models.CartItem;
import com.mmf.plantpal.models.Item;
import com.mmf.plantpal.repository.DataRepository;
import com.mmf.plantpal.utilteis.MoneyFormatter;

import java.util.ArrayList;
import java.util.List;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartItemVewHolder> {
    private Context context;
    private List<CartItem> cartItemList;

    FragmentCartBinding binding;

    private OnItemCartClickListener onItemCartClickListener;


    public CartItemAdapter(Context context, FragmentCartBinding binding) {
        this.context = context;
        this.cartItemList = new ArrayList<>();
        this.binding = binding;
    }


    public static class CartItemVewHolder extends RecyclerView.ViewHolder {
        CartItemLayoutBinding binding;

        public CartItemVewHolder(View view) {
            super(view);
            binding = CartItemLayoutBinding.bind(view);
        }
    }


    @NonNull
    @Override
    public CartItemVewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout, parent, false);
        return new CartItemVewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CartItemVewHolder holder, final int position) {

        CartItem cartItem = cartItemList.get(position);

        holder.binding.itemName.setText(cartItem.getName());
        holder.binding.itemPrice.setText(MoneyFormatter.fromNumberToMoneyFormat(cartItem.getPrice()));
        holder.binding.cartItemQuantity.setText(String.valueOf(cartItem.getCount()));


        if (cartItem.getImagePath() == null || cartItem.getImagePath().isEmpty()) {
            holder.binding.itemImage.setImageResource(R.drawable.plantimage);
        } else {
            Glide.with(context)
                    .load(cartItem.getImagePath())
                    .error(R.drawable.plantimage)
                    .placeholder(R.drawable.plantimage)
                    .into(holder.binding.itemImage);
        }






        holder.binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemCartClickListener == null) return;

                onItemCartClickListener.onButtonRemoveClick(cartItem.getReferenceId());
            }
        });




        holder.binding.cartItemPlusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartItem.setCount(cartItem.getCount() + 1);
                customNotify();
            }
        });




        holder.binding.cartItemMinusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cartItem.getCount() == 1)return;

                cartItem.setCount(cartItem.getCount() - 1);
                customNotify();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public void setCartItemList(List<CartItem> cartItemList) {
        this.cartItemList = cartItemList;
        customNotify();
    }


    private void customNotify() {
        notifyDataSetChanged();

        if (DataRepository.getItemsCart().size() == 0) {
            binding.progressHorizontalLayout.setVisibility(View.VISIBLE);
            binding.state.setText(context.getString(R.string.str_no_cart_item));
            binding.progressHorizontal.setVisibility(View.GONE);
            binding.btnCheckout.setVisibility(View.GONE);
            binding.btnCheckout.setText(MoneyFormatter.fromNumberToMoneyFormat(0));
        } else {
            binding.progressHorizontalLayout.setVisibility(View.GONE);
            binding.btnCheckout.setVisibility(View.VISIBLE);


            String checkoutText =
                    context.getString(R.string.str_checkout) +
                            "  " +
                            MoneyFormatter.fromNumberToMoneyFormat(getInvoiceSum());

            binding.btnCheckout.setText(checkoutText);

        }


    }

    public double getInvoiceSum() {

        return cartItemList.stream()
                .mapToDouble(CartItem::getPrice).sum();


//        float sum = 0;
//
//        for (Item item : itemList) {
//            sum += item.getPrice();
//        }
//        return sum;

    }

    public void setOnItemCartClickListener(OnItemCartClickListener onItemCartClickListener) {
        this.onItemCartClickListener = onItemCartClickListener;
    }

    public interface OnItemCartClickListener {
        void onButtonRemoveClick(String itemReference);
    }
}



