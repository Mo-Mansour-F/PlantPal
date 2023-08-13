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
import com.mmf.plantpal.models.Item;
import com.mmf.plantpal.repository.DataRepository;
import com.mmf.plantpal.utilteis.MoneyFormatter;

import java.util.ArrayList;
import java.util.List;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartItemVewHolder> {
    private Context context;
    private List<Item> itemList;

    FragmentCartBinding binding;

    private OnItemCartClickListener onItemCartClickListener;


    public CartItemAdapter(Context context, FragmentCartBinding binding) {
        this.context = context;
        this.itemList = new ArrayList<>();
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

        Item item = itemList.get(position);

        holder.binding.itemName.setText(item.getName());
        holder.binding.itemPrice.setText(MoneyFormatter.fromNumberToMoneyFormat(item.getPrice()));


        holder.binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemCartClickListener == null) return;

                onItemCartClickListener.onButtonRemoveClick(item);
            }
        });


        if (item.getImagePath() == null || item.getImagePath().isEmpty()) {
            holder.binding.itemImage.setImageResource(R.drawable.plantimage);
        } else {
            Glide.with(context)
                    .load(item.getImagePath())
                    .error(R.drawable.plantimage)
                    .placeholder(R.drawable.plantimage)
                    .into(holder.binding.itemImage);
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
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

        return itemList.stream()
                .mapToDouble(Item::getPrice).sum();


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
        void onButtonRemoveClick(Item item);
    }
}



