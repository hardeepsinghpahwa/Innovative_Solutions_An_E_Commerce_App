package com.example.itshop;

public class returnitem {
    String id,quantity,price,discount;

    public returnitem(String id, String quantity, String price, String discount) {
        this.id = id;
        this.quantity = quantity;
        this.price = price;
        this.discount = discount;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public returnitem(String id, String quantity) {
        this.id = id;
        this.quantity = quantity;
    }

    public returnitem() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
