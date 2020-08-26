package com.example.itshop;

public class itemdetails {
    String image,name,rating,price,itemid,discount,stock;
    int draw;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public itemdetails() {
    }

    public itemdetails(String image) {
        this.image = image;
    }

    public itemdetails(String image, String name) {
        this.image = image;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public itemdetails(String image, String name, String rating, String price) {
        this.image = image;
        this.name = name;
        this.rating = rating;
        this.price = price;
    }



    public itemdetails(String name, int draw) {
        this.name = name;
        this.draw = draw;
    }

    public itemdetails(String image, String name, String rating, String price, String discount) {
        this.image = image;
        this.name = name;
        this.rating = rating;
        this.price = price;
        this.discount = discount;
    }

    public itemdetails(String name, String rating, String price) {
        this.name = name;
        this.rating = rating;
        this.price = price;
    }

    public int getDraw() {
        return draw;
    }

    public void setDraw(int draw) {
        this.draw = draw;
    }

    public String getItemid() {
        return itemid;
    }

    public void setItemid(String itemid) {
        this.itemid = itemid;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getStock() {

        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }
}
