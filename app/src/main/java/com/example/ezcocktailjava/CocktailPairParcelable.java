package com.example.ezcocktailjava;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pair;

//para poder pasar las Pairs de Strings ingrediente/cantidad tiene que ser parcelable
public class CocktailPairParcelable extends Pair<String, String> implements Parcelable {
    public CocktailPairParcelable(String first, String second) {
        super(first, second);
    }
    protected CocktailPairParcelable(Parcel in) {
        super(in.readString(),in.readString());
    }
    public static final Creator<CocktailPairParcelable> CREATOR = new Creator<CocktailPairParcelable>() {
        @Override
        public CocktailPairParcelable createFromParcel(Parcel in) {
            return new CocktailPairParcelable(in);
        }
        @Override
        public CocktailPairParcelable[] newArray(int size) {
            return new CocktailPairParcelable[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.first);
        parcel.writeString(this.second);
    }
};