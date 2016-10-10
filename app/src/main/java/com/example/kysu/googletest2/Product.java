package com.example.kysu.googletest2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by K.Y.Su on 2016-10-01.
 */
public class Product {
    private String pTitle;
    private String pUrl;
    private Double pwido;
    private Double pgugdo;
    private String pmlevel;


    public String getpTitle() {
        return pTitle;
    }

    public void setpTitle(String pTitle) {
        this.pTitle = pTitle;
    }

    public String getpUrl() {
        return pUrl;
    }

    public void setpUrl(String pUrl) {
        this.pUrl = pUrl;
    }

    public Double getPwido() {
        return pwido;
    }

    public void setPwido(Double pwido) {
        this.pwido = pwido;
    }

    public Double getPgugdo() {
        return pgugdo;
    }

    public void setPgugdo(Double pgugdo) {
        this.pgugdo = pgugdo;
    }

    public String getPmlevel() {
        return pmlevel;
    }

    public void setPmlevel(String pmlevel) {
        this.pmlevel = pmlevel;
    }
}
