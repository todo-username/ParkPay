package com.example.parkpay;

import java.util.ArrayList;
import java.util.List;

class Sale {
    String name;
    String text;
    String dateStart;
    String dateEnd;
    String photo;

    Sale(
            String name,
            String text,
            String dateStart,
            String dateEnd,
            String photo
    ) {
        this.name = name;
        this.text = text;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.photo = photo;
    }
    public String getImageUrl() {
        return photo;
    }

    public void setImageUrl(String imageUrl) {
        this.photo = imageUrl;
    }
}