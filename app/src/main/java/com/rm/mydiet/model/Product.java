package com.rm.mydiet.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by alex
 */
public class Product implements Parcelable {
    private String name;
    private String info;
    private String id;
    private String img;
    private float proteins;
    private float fats;
    private float carbohydrates;
    private int calories;

    public Product() {}

    protected Product(Parcel in) {
        name = in.readString();
        info = in.readString();
        id = in.readString();
        img = in.readString();
        proteins = in.readFloat();
        fats = in.readFloat();
        carbohydrates = in.readFloat();
        calories = in.readInt();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getProteins() {
        return proteins;
    }

    public void setProteins(float proteins) {
        this.proteins = proteins;
    }

    public float getFats() {
        return fats;
    }

    public void setFats(float fats) {
        this.fats = fats;
    }

    public float getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(float carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(info);
        dest.writeString(id);
        dest.writeString(img);
        dest.writeFloat(proteins);
        dest.writeFloat(fats);
        dest.writeFloat(carbohydrates);
        dest.writeInt(calories);
    }
}
