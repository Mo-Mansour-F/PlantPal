package com.mmf.plantpal.models;

public class CartItem extends Item {
    private int count;
    private float sum;

    public CartItem() {
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public float getSum() {
        return sum;
    }

    public void setSum(float sum) {
        this.sum = sum;
    }


    @Override
    public float getPrice() {
        return super.getPrice() * this.count;
    }
}
