package com.example.itshop;

public class itemreview {
    String uid,review,rating;

    public itemreview(String uid, String review, String rating) {
        this.uid = uid;
        this.review = review;
        this.rating = rating;
    }

    public itemreview() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
